package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.OrderDtos;
import com.example.petshopbackend.entity.Order;

public interface OrderService extends IService<Order> {

    /**
     * 从购物车创建订单
     * @param createDto 包含地址ID和购物车项ID的DTO
     * @param username 当前登录的用户名
     * @return 创建成功的订单信息
     */
    Order createOrder(OrderDtos.OrderCreateDto createDto, String username);

    //获取指定用户的订单列表（分页）
    Page<Order> listUserOrders(String username, Page<Order> page);

    //获取单个订单的详细信息
    OrderDtos.OrderViewDto getOrderDetails(String orderNo, String username);
}
