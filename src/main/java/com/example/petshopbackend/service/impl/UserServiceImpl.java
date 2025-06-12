package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserDtos.UserProfileDto getUserProfileByUsername(String username) {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return UserDtos.UserProfileDto.fromUser(user);
    }

    /**
     * [ADDED] 实现更新用户头像的逻辑
     */
    @Override
    public void updateUserAvatar(String username, String avatarUrl) {
        // 使用 LambdaUpdateWrapper 可以更高效地只更新单个字段
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(User::getUsername, username) // 定位到要更新的用户
                .set(User::getAvatarUrl, avatarUrl); // 只设置 avatar_url 字段的新值

        // 执行更新
        boolean updated = this.update(updateWrapper);

        if (!updated) {
            throw new RuntimeException("用户不存在或更新失败");
        }
    }
}
