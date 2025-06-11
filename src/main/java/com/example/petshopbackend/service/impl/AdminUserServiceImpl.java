package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // 注入密码编码器

    @Override
    public Page<AdminDtos.AdminUserViewDto> listUsers(Page<Object> page) {
        // 1. 调用MyBatis-Plus的分页查询，查询User实体
        Page<User> userPage = userMapper.selectPage(new Page<>(page.getCurrent(), page.getSize()), null);

        // 2. 创建一个新的分页对象，用于存放DTO
        Page<AdminDtos.AdminUserViewDto> dtoPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());

        // 3. 将查询到的User实体列表，转换为AdminUserViewDto列表
        List<AdminDtos.AdminUserViewDto> dtoList = userPage.getRecords().stream()
                .map(AdminDtos.AdminUserViewDto::fromUser)
                .toList();

        // 4. 将DTO列表设置到新的分页对象中
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public void createUser(AdminDtos.AdminUserCreateDto createDto) {
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, createDto.getUsername()))) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(createDto.getUsername());
        user.setPassword(passwordEncoder.encode(createDto.getPassword())); // 密码必须加密
        user.setNickname(createDto.getNickname());
        user.setRole(createDto.getRole());
        userMapper.insert(user);
    }

    @Override
    public void updateUser(Long userId, AdminDtos.AdminUserUpdateDto updateDto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setNickname(updateDto.getNickname());
        user.setRole(updateDto.getRole());
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long userId, String currentAdminUsername) {
        User userToDelete = userMapper.selectById(userId);
        if (userToDelete == null) {
            throw new RuntimeException("用户不存在");
        }
        // 安全措施：防止管理员删除自己
        if (userToDelete.getUsername().equals(currentAdminUsername)) {
            throw new RuntimeException("管理员不能删除自己");
        }
        userMapper.deleteById(userId);
    }
}
