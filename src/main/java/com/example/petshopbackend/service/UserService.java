package com.example.petshopbackend.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.entity.User;

public interface UserService extends IService<User> {

    UserDtos.UserProfileDto getUserProfileByUsername(String username);
    /**
     * [ADDED] 更新指定用户的头像
     * @param username 用户名
     * @param avatarUrl 新的头像URL
     */
    void updateUserAvatar(String username, String avatarUrl);
}
