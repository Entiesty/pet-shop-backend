package com.example.petshopbackend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Address;
import com.example.petshopbackend.service.AddressService;
import com.example.petshopbackend.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台-用户管理模块", description = "提供对用户账户的分页查询等后台管理功能")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AddressService addressService;
    
    @Operation(summary = "分页获取用户列表", description = "返回分页形式的用户视图数据，仅限管理员访问")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminDtos.AdminUserViewDto>> getUserList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量", example = "10")
            @RequestParam(defaultValue = "10") long size
    ) {
        Page<Object> page = new Page<>(current, size);
        return ResponseEntity.ok(adminUserService.listUsers(page));
    }

    @Operation(summary = "创建新用户（包括管理员）")
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody AdminDtos.AdminUserCreateDto createDto) {
        adminUserService.createUser(createDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "更新指定ID的用户信息")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody AdminDtos.AdminUserUpdateDto updateDto) {
        adminUserService.updateUser(id, updateDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "删除指定ID的用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        adminUserService.deleteUser(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取指定用户的收货地址列表")
    @GetMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Address>> getUserAddresses(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }
}
