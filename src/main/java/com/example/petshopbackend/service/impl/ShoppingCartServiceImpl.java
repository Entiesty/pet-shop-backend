package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.CartDtos;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.ShoppingCart;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.ProductMapper;
import com.example.petshopbackend.mapper.ShoppingCartMapper;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @Override
    public void addOrUpdateItem(CartDtos.CartItemAddDto itemDto, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 查找购物车中是否已存在该商品
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, user.getId())
                .eq(ShoppingCart::getProductId, itemDto.getProductId());
        ShoppingCart existingItem = baseMapper.selectOne(wrapper);

        if (existingItem != null) {
            // 如果存在，更新数量
            existingItem.setQuantity(itemDto.getQuantity());
            baseMapper.updateById(existingItem);
        } else {
            // 如果不存在，新增记录
            ShoppingCart newItem = new ShoppingCart();
            newItem.setUserId(user.getId());
            newItem.setProductId(itemDto.getProductId());
            newItem.setQuantity(itemDto.getQuantity());
            baseMapper.insert(newItem);
        }
    }

    @Override
    public CartDtos.CartViewDto getCartForUser(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        List<ShoppingCart> cartItems = baseMapper.selectList(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, user.getId()));

        CartDtos.CartViewDto cartView = new CartDtos.CartViewDto();
        if (CollectionUtils.isEmpty(cartItems)) {
            cartView.setItems(Collections.emptyList());
            cartView.setTotalPrice(BigDecimal.ZERO);
            return cartView;
        }

        // 批量查询商品详情
        List<Long> productIds = cartItems.stream().map(ShoppingCart::getProductId).collect(Collectors.toList());
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 组装视图
        List<CartDtos.CartItemView> itemViews = cartItems.stream().map(item -> {
            Product product = productMap.get(item.getProductId());
            CartDtos.CartItemView itemView = new CartDtos.CartItemView();
            itemView.setProductId(item.getProductId());
            itemView.setQuantity(item.getQuantity());
            if (product != null) {
                itemView.setName(product.getName());
                itemView.setMainImageUrl(product.getMainImageUrl());
                itemView.setPrice(product.getPrice());
                itemView.setStock(product.getStock());
            }
            return itemView;
        }).collect(Collectors.toList());

        // 计算总价
        BigDecimal totalPrice = itemViews.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartView.setItems(itemViews);
        cartView.setTotalPrice(totalPrice);
        return cartView;
    }

    @Override
    public void removeItem(Long productId, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, user.getId())
                .eq(ShoppingCart::getProductId, productId);
        baseMapper.delete(wrapper);
    }
}
