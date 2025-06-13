package com.example.petshopbackend.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Schema(description = "商品相关的数据传输对象")
public class ProductDtos {

    @Data
    @Schema(description = "用于前端展示的商品详情聚合视图对象 (优化版)")
    public static class ProductDetailViewDto {

        // --- 商品核心信息 ---
        @Schema(description = "商品ID")
        private Long id;
        @Schema(description = "商品名称")
        private String name;
        @Schema(description = "价格")
        private BigDecimal price;
        @Schema(description = "主图URL")
        private String mainImageUrl;
        @Schema(description = "介绍视频URL")
        private String videoUrl;
        @Schema(description = "库存")
        private Integer stock;
        @Schema(description = "商品描述")
        private String description;

        // --- 宠物专属属性 ---
        @Schema(description = "品种")
        private String breed;
        @Schema(description = "年龄")
        private String age;
        @Schema(description = "性别")
        private String sex;
        @Schema(description = "体重(kg)")
        private BigDecimal weight;
        @Schema(description = "颜色")
        private String color;
        @Schema(description = "健康信息")
        private String healthInfo;

        // --- 评价统计 ---
        @Schema(description = "平均评分")
        private BigDecimal averageRating;
        @Schema(description = "评价总数")
        private Integer reviewCount;

        // --- 关联信息 ---
        @Schema(description = "商品所属商店信息")
        private StoreInfo store;

        @Schema(description = "商品评价的分页列表 (第一页)")
        private Page<ReviewDtos.ReviewViewDto> reviews;

        @Data
        @Schema(description = "详情页中简化的商店信息")
        public static class StoreInfo {
            private Long id;
            private String name;
            private String logoUrl;
        }
    }

    /**
     * 用于前端列表展示的商品视图DTO，增加了isPet标志
     */
    @Data
    @EqualsAndHashCode(callSuper = true) // [ADDED] 告诉Lombok在比较时包含父类字段
    @Schema(description = "商品列表视图DTO")
    public static class ProductListViewDto extends Product {
        @Schema(description = "是否为活体宠物")
        private boolean isPet;
    }
}