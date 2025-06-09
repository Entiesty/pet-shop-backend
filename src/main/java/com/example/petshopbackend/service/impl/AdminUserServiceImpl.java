package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

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
}
