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
        @Schema(description = "商店名称", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "商店的详细文本地址", requiredMode = Schema.RequiredMode.REQUIRED)
        private String addressText;

        @Schema(description = "商店Logo的URL")
        private String logoUrl;

        @Schema(description = "联系电话")
        private String contactPhone;

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
        @Schema(description = "所属分类ID")
        private Long categoryId;
        @Schema(description = "商品名称")
        private String name;
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
        @Schema(description = "商品描述")
        private String description;
        @Schema(description = "健康信息")
        private String healthInfo;
        @Schema(description = "价格")
        private BigDecimal price;
        @Schema(description = "库存")
        private Integer stock;
        @Schema(description = "商品主图URL")
        private String mainImageUrl;
        @Schema(description = "介绍视频URL")
        private String videoUrl;
    }

    /**
     * [ADDED] 用于后台创建新用户的DTO
     */
    @Data
    @Schema(description = "后台创建新用户的DTO")
    public static class AdminUserCreateDto {
        @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
        private String username;
        @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
        private String password;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "角色 (0-会员, 1-管理员)", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer role;
    }

    /**
     * [ADDED] 用于后台更新用户信息的DTO
     */
    @Data
    @Schema(description = "后台更新用户信息的DTO")
    public static class AdminUserUpdateDto {
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "角色 (0-会员, 1-管理员)")
        private Integer role;
        // 注意：通常不通过此接口直接更新密码，密码重置应有专门的接口
    }

    @Data
    @Schema(description = "后台订单发货请求体")
    public static class OrderShipmentDto {
        @Schema(description = "物流公司名称", requiredMode = Schema.RequiredMode.REQUIRED)
        private String shippingCarrier;

        @Schema(description = "物流单号", requiredMode = Schema.RequiredMode.REQUIRED)
        private String trackingNumber;
    }
}
