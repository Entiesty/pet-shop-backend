package com.example.petshopbackend.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "商品相关的数据传输对象")
public class ProductDtos {

    @Data
    @Schema(description = "用于前端展示的商品详情聚合视图对象")
    public static class ProductDetailViewDto {
        @Schema(description = "商品基本信息")
        private Product product;

        @Schema(description = "商品所属商店信息")
        private Store store;

        @Schema(description = "商品评价的分页列表")
        private Page<ReviewDtos.ReviewViewDto> reviews;
    }
}
