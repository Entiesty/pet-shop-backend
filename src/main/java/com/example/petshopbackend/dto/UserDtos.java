package com.example.petshopbackend.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "用户相关的DTOs")
public class UserDtos {

    @Data
    @Schema(description = "用户个人资料信息DTO")
    public static class UserProfileDto {

        @Schema(description = "用户ID", example = "1")
        private Long id;

        @Schema(description = "登录用户名", example = "user001")
        private String username;

        @Schema(description = "用户昵称", example = "小明")
        private String nickname;

        @Schema(description = "用户头像URL", example = "http://example.com/avatars/user001.png")
        private String avatarUrl;

        @Schema(description = "用户角色（例如：0-普通用户, 1-管理员）", example = "0")
        private Integer role;

        public static UserProfileDto fromUser(User user) {
            UserProfileDto dto = new UserProfileDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setAvatarUrl(user.getAvatarUrl());
            dto.setRole(user.getRole());
            return dto;
        }
    }

    /**
     * [ADDED] 用于前端展示的商店详情聚合视图对象
     */
    @Data
    @Schema(description = "用户端商店完整详情视图DTO")
    public static class UserStoreDetailViewDto {
        @Schema(description = "商店ID")
        private Long id;
        @Schema(description = "商店名称")
        private String name;
        @Schema(description = "详细文本地址")
        private String addressText;
        @Schema(description = "Logo URL")
        private String logoUrl;
        @Schema(description = "联系电话")
        private String contactPhone;
        @Schema(description = "经度 (来自MongoDB)")
        private Double longitude;
        @Schema(description = "纬度 (来自MongoDB)")
        private Double latitude;

        // 增强功能：同时返回该店铺下的部分商品
        @Schema(description = "该商店下的商品分页列表 (第一页)")
        private Page<Product> products;
    }

    /**
     * [ADDED] 用于用户更新头像的请求体DTO
     */
    @Data
    @Schema(description = "更新用户头像请求体")
    public static class AvatarUpdateDto {
        @Schema(description = "新的头像文件URL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "头像URL不能为空")
        private String avatarUrl;
    }
}