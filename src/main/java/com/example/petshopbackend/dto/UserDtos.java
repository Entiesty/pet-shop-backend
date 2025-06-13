package com.example.petshopbackend.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "面向用户端的相关数据传输对象")
public class UserDtos {

    @Data
    @Schema(description = "用户个人资料视图DTO")
    public static class UserProfileDto {
        @Schema(description = "用户ID")
        private Long id;
        @Schema(description = "登录用户名")
        private String username;
        @Schema(description = "用户昵称")
        private String nickname;
        @Schema(description = "电子邮箱")
        private String email;
        @Schema(description = "手机号码")
        private String phone;
        @Schema(description = "用户头像URL")
        private String avatarUrl;
        @Schema(description = "用户角色 (0-会员, 1-管理员)")
        private Integer role;
        @Schema(description = "账户余额")
        private BigDecimal balance;

        public static UserProfileDto fromUser(User user) {
            UserProfileDto dto = new UserProfileDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setAvatarUrl(user.getAvatarUrl());
            dto.setRole(user.getRole());
            dto.setBalance(user.getBalance());
            return dto;
        }
    }

    @Data
    @Schema(description = "更新用户个人资料请求体")
    public static class ProfileUpdateDto {
        @Schema(description = "新的昵称")
        private String nickname;
        @Schema(description = "新的手机号")
        private String phone;
        @Schema(description = "新的电子邮箱")
        private String email;
    }

    @Data
    @Schema(description = "修改密码请求体")
    public static class PasswordUpdateDto {
        @Schema(description = "旧密码", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty
        private String oldPassword;

        @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty
        private String newPassword;
    }

    @Data
    @Schema(description = "更新用户头像请求体")
    public static class AvatarUpdateDto {
        @Schema(description = "新的头像文件URL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "头像URL不能为空")
        private String avatarUrl;
    }

    /**
     * 用于前端展示的商店详情聚合视图对象
     * 这个DTO聚合了MySQL和MongoDB的数据，并包含了商店下的部分商品信息。
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
        // [MODIFIED] 确保这里的类型是 Page<ProductListViewDto>
        @Schema(description = "该商店下的商品分页列表 (第一页)")
        private Page<ProductDtos.ProductListViewDto> products;
    }
}