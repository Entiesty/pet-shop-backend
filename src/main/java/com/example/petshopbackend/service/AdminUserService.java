package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;

public interface AdminUserService {
    /**
     * 分页查询所有用户
     * @param page 分页对象
     * @return 包含用户视图DTO的分页结果
     */
    Page<AdminDtos.AdminUserViewDto> listUsers(Page<Object> page);

    // [ADDED] 新增接口方法
    void createUser(AdminDtos.AdminUserCreateDto createDto);
    void updateUser(Long userId, AdminDtos.AdminUserUpdateDto updateDto);
    void deleteUser(Long userId, String currentAdminUsername);
}
