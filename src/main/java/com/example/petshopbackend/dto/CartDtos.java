package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.ShoppingCart;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

public class CartDtos {

    /**
     * 用于接收前端“添加/更新购物车”请求的DTO
     */
    @Data
    public static class CartItemAddDto {
        private Long productId;
        private Integer quantity;
    }

    /**
     * 用于向前端“展示购物车”的DTO
     */
    @Data
    public static class CartViewDto {
        private List<CartItemView> items;
        private BigDecimal totalPrice; // 购物车总价
    }

    /**
     * 购物车中单个商品的视图对象
     */
    @Data
    public static class CartItemView {
        private Long productId;
        private String name;
        private String mainImageUrl;
        private BigDecimal price;
        private Integer quantity; // 用户选择的数量
        private Integer stock;    // 商品当前库存
    }
}
