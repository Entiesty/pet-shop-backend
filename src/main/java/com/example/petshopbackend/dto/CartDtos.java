package com.example.petshopbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "购物车相关的DTOs")
public class CartDtos {

    /**
     * 用于接收前端“添加/更新购物车”请求的DTO
     */
    @Data
    @Schema(description = "用于接收前端“添加/更新购物车”请求的DTO")
    public static class CartItemAddDto {

        @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long productId;

        @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", minimum = "1")
        private Integer quantity;
    }

    /**
     * 用于向前端“展示购物车”的DTO
     */
    @Data
    @Schema(description = "用于向前端“展示购物车”的DTO")
    public static class CartViewDto {

        @Schema(description = "购物车商品项目列表")
        private List<CartItemView> items;

        @Schema(description = "购物车总价", example = "999.50")
        private BigDecimal totalPrice;
    }

    /**
     * 购物车中单个商品的视图对象
     */
    @Data
    @Schema(description = "购物车中单个商品的视图对象")
    public static class CartItemView {

        @Schema(description = "商品ID", example = "1")
        private Long productId;

        @Schema(description = "商品名称", example = "豪华猫爬架")
        private String name;

        @Schema(description = "商品主图URL", example = "http://example.com/images/cat-tree.jpg")
        private String mainImageUrl;

        @Schema(description = "商品单价", example = "499.75")
        private BigDecimal price;

        @Schema(description = "用户选择的数量", example = "2")
        private Integer quantity;

        @Schema(description = "商品当前库存", example = "50")
        private Integer stock;
    }
}