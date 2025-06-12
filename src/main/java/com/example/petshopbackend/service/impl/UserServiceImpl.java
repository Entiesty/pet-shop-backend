package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDtos.UserProfileDto getUserProfileByUsername(String username) {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return UserDtos.UserProfileDto.fromUser(user);
    }

    @Override
    public void updateUserAvatar(String username, String avatarUrl) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(User::getUsername, username)
                .set(User::getAvatarUrl, avatarUrl);

        if (!this.update(updateWrapper)) {
            throw new RuntimeException("用户不存在或更新失败");
        }
    }

    @Override
    public void updateUserProfile(String username, UserDtos.ProfileUpdateDto updateDto) {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        boolean needsUpdate = false;
        if (StringUtils.hasText(updateDto.getNickname())) {
            user.setNickname(updateDto.getNickname());
            needsUpdate = true;
        }
        if (StringUtils.hasText(updateDto.getPhone())) {
            user.setPhone(updateDto.getPhone());
            needsUpdate = true;
        }
        if (StringUtils.hasText(updateDto.getEmail())) {
            user.setEmail(updateDto.getEmail());
            needsUpdate = true;
        }

        if (needsUpdate) {
            baseMapper.updateById(user);
        }
    }

    @Override
    public void updatePassword(String username, UserDtos.PasswordUpdateDto passwordDto) {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        baseMapper.updateById(user);
    }
}