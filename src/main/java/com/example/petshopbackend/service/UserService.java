package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.entity.User;

public interface UserService extends IService<User> {

    /**
     * 根据用户名获取用户个人资料
     */
    UserDtos.UserProfileDto getUserProfileByUsername(String username);

    /**
     * 更新指定用户的头像
     */
    void updateUserAvatar(String username, String avatarUrl);

    /**
     * 更新指定用户的个人资料（昵称、手机、邮箱）
     */
    void updateUserProfile(String username, UserDtos.ProfileUpdateDto updateDto);

    /**
     * 更新指定用户的密码
     */
    void updatePassword(String username, UserDtos.PasswordUpdateDto passwordDto);
}