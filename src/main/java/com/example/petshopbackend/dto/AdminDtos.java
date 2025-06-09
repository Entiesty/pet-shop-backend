package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.User;
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
    public static class StoreDto {
        private String name;
        private String addressText;
        private String logoUrl;
        private String contactPhone;
        // 创建商店时必须提供经纬度，用于地图功能
        private Double longitude;
        private Double latitude;
    }

    @Data
    public static class ProductDto {
        private Long storeId;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer productType; // 1-宠物, 2-周边
        private Integer stock;
        private String mainImageUrl;
        private String videoUrl;
    }
}
