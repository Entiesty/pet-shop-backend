package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminDtos {

    /**
     * 用于后台展示用户的DTO
     */
    @Data
    public static class AdminUserViewDto {
        private Long id;
        private String username;
        private String nickname;
        private String avatarUrl;
        private Integer role; // 0-会员, 1-管理员
        private LocalDateTime createdAt;

        public static AdminUserViewDto fromUser(User user) {
            AdminUserViewDto dto = new AdminUserViewDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setAvatarUrl(user.getAvatarUrl());
            dto.setRole(user.getRole());
            dto.setCreatedAt(user.getCreatedAt());
            return dto;
        }
    }

    /**
     * [ADDED] 用于后台创建或更新商店的DTO
     */
    @Data
    @Schema(description = "后台创建或更新商店的DTO")
    public static class StoreDto {
        private String name;
        private String addressText;
        private String logoUrl;
        private String contactPhone;
        // 👇 这里已经准备好接收前端传来的经纬度
        @Schema(description = "经度", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double longitude;
        @Schema(description = "纬度", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double latitude;
    }

    @Data
    @Schema(description = "后台创建或更新商品的DTO")
    public static class ProductDto {
        @Schema(description = "所属商店ID")
        private Long storeId;

        @Schema(description = "所属分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long categoryId; // <-- 确认这个字段存在

        @Schema(description = "商品名称")
        private String name;

        @Schema(description = "商品描述")
        private String description;

        @Schema(description = "价格")
        private BigDecimal price;

        @Schema(description = "库存")
        private Integer stock;

        @Schema(description = "商品主图URL")
        private String mainImageUrl;

        @Schema(description = "介绍视频URL")
        private String videoUrl;
    }
}
