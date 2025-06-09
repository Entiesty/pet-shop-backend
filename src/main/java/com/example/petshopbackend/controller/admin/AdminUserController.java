package com.example.petshopbackend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 分页获取用户列表
     * 只有ADMIN角色的用户才能访问
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 提供了更细粒度的、基于方法的权限控制
    public ResponseEntity<Page<AdminDtos.AdminUserViewDto>> getUserList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        Page<Object> page = new Page<>(current, size);
        return ResponseEntity.ok(adminUserService.listUsers(page));
    }
}
