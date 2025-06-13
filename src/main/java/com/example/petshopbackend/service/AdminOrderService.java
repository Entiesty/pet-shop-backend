package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Order;

public interface AdminOrderService extends IService<Order> {
    void shipOrder(String orderNo, AdminDtos.OrderShipmentDto shipmentDto);
    // [ADDED]
    Page<Order> listOrders(Page<Order> page, String orderNo, Integer status);
    void updateOrderStatus(String orderNo, AdminDtos.AdminOrderStatusUpdateDto statusDto);
}
