package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.CartDtos;
import com.example.petshopbackend.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加或更新购物车中的商品
     */
    void addOrUpdateItem(CartDtos.CartItemAddDto itemDto, String username);

    /**
     * 获取用户的购物车视图
     */
    CartDtos.CartViewDto getCartForUser(String username);

    /**
     * 从购物车中移除商品
     */
    void removeItem(Long productId, String username);
}
