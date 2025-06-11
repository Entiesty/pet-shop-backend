package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Order;
import com.example.petshopbackend.mapper.OrderMapper;
import com.example.petshopbackend.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements AdminOrderService {

    @Override
    public void shipOrder(String orderNo, AdminDtos.OrderShipmentDto shipmentDto) {
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 20) throw new RuntimeException("订单状态不为“待发货”，无法操作");

        order.setStatus(30); // 更新状态为30: 待收货
        order.setShippingCarrier(shipmentDto.getShippingCarrier());
        order.setTrackingNumber(shipmentDto.getTrackingNumber());
        baseMapper.updateById(order);
    }
}