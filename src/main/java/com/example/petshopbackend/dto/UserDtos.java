package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.User;
import lombok.Data;

public class UserDtos {

    @Data
    public static class UserProfileDto {
        private Long id;
        private String username;
        private String nickname;
        private String avatarUrl;
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
