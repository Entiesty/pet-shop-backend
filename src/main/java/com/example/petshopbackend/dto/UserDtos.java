package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
}