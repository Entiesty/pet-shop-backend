package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.OrderDtos;
import com.example.petshopbackend.entity.*;
import com.example.petshopbackend.mapper.*;
import com.example.petshopbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final ProductMapper productMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // *** 核心：声明此方法为事务性 ***
    public Order createOrder(OrderDtos.OrderCreateDto createDto, String username) {
        // --- 1. 数据准备与校验 ---
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Address address = addressMapper.selectById(createDto.getAddressId());
        if (address == null || !address.getUserId().equals(user.getId())) {
            throw new RuntimeException("收货地址无效");
        }

        List<ShoppingCart> cartItems = shoppingCartMapper.selectBatchIds(createDto.getCartItemIds());
        if (CollectionUtils.isEmpty(cartItems) || cartItems.stream().anyMatch(item -> !item.getUserId().equals(user.getId()))) {
            throw new RuntimeException("购物车选项无效");
        }

        // --- 2. 检查库存并锁定商品信息 ---
        List<Long> productIds = cartItems.stream().map(ShoppingCart::getProductId).collect(Collectors.toList());
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (ShoppingCart item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品 " + (product != null ? product.getName() : "ID:" + item.getProductId()) + " 库存不足");
            }
        }

        // --- 3. 创建订单主表记录 ---
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> productMap.get(item.getProductId()).getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(user.getId());
        order.setAddressId(address.getId());
        order.setTotalAmount(totalAmount);
        order.setStatus(10); // 10: 待付款
        order.setCreatedAt(LocalDateTime.now());
        baseMapper.insert(order); // 插入后，order对象的ID会被自动填充

        // --- 4. 创建订单项并扣减库存 ---
        for (ShoppingCart item : cartItems) {
            Product product = productMap.get(item.getProductId());

            // 插入订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice()); // 记录下单时的单价
            orderItemMapper.insert(orderItem);

            // 扣减库存
            product.setStock(product.getStock() - item.getQuantity());
            productMapper.updateById(product);
        }

        // --- 5. 清理购物车 ---
        shoppingCartMapper.deleteByIds(createDto.getCartItemIds());

        return order;
    }

    /**
     * [ADDED] 获取用户的订单列表（分页）
     */
    @Override
    public Page<Order> listUserOrders(String username, Page<Order> page) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, user.getId())
                .orderByDesc(Order::getCreatedAt); // 按创建时间降序排序

        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * [ADDED] 获取单个订单的详细信息
     */
    @Override
    public OrderDtos.OrderViewDto getOrderDetails(String orderNo, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 1. 查询订单主信息
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));

        // 2. 验证订单是否存在以及是否属于当前用户
        if (order == null || !order.getUserId().equals(user.getId())) {
            throw new RuntimeException("订单不存在或无权访问");
        }

        // 3. 查询订单包含的商品项
        List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );

        // 4. 批量查询商品详情
        List<Long> productIds = orderItems.stream().map(OrderItem::getProductId).collect(Collectors.toList());
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 5. 查询收货地址信息
        Address address = addressMapper.selectById(order.getAddressId());

        // 6. 组装成OrderViewDto返回给前端
        OrderDtos.OrderViewDto dto = new OrderDtos.OrderViewDto();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        if (address != null) {
            dto.setContactName(address.getContactName());
            dto.setPhone(address.getPhone());
            dto.setFullAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getStreet());
        }

        List<OrderDtos.OrderItemView> itemViews = orderItems.stream().map(item -> {
            Product product = productMap.get(item.getProductId());
            OrderDtos.OrderItemView itemView = new OrderDtos.OrderItemView();
            itemView.setProductId(item.getProductId());
            itemView.setQuantity(item.getQuantity());
            itemView.setUnitPrice(item.getUnitPrice());
            if (product != null) {
                itemView.setName(product.getName());
                itemView.setMainImageUrl(product.getMainImageUrl());
            }
            return itemView;
        }).collect(Collectors.toList());

        dto.setItems(itemViews);

        return dto;
    }

    @Override
    @Transactional
    public void payForOrder(String orderNo, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));

        // --- 业务校验 ---
        if (order == null || !order.getUserId().equals(user.getId())) throw new RuntimeException("订单不存在或无权操作");
        if (order.getStatus() != 10) throw new RuntimeException("订单状态不正确，无法支付");
        if (user.getBalance().compareTo(order.getTotalAmount()) < 0) throw new RuntimeException("用户余额不足");

        // --- 核心逻辑 ---
        // 1. 扣减用户余额
        user.setBalance(user.getBalance().subtract(order.getTotalAmount()));
        userMapper.updateById(user);

        // 2. 更新订单状态
        order.setStatus(20); // 状态20：待发货
        order.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(order);
    }

    @Override
    public void confirmReceipt(String orderNo, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));

        if (order == null || !order.getUserId().equals(user.getId())) throw new RuntimeException("订单不存在或无权操作");
        if (order.getStatus() != 30) throw new RuntimeException("订单状态不为“待收货”，无法操作");

        order.setStatus(40); // 更新状态为40: 已完成
        baseMapper.updateById(order);
    }
}
