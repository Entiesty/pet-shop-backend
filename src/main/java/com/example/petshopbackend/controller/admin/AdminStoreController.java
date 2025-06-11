package com.example.petshopbackend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.service.AdminStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-商店管理模块", description = "提供对宠物商店的增删改查等管理功能")
@RestController
@RequestMapping("/api/admin/stores")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStoreController {

    private final AdminStoreService adminStoreService;

    @Operation(summary = "新增宠物商店")
    @PostMapping
    public ResponseEntity<Void> createStore(@RequestBody AdminDtos.StoreDto storeDto) {
        adminStoreService.createStore(storeDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "更新指定ID的商店信息")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStore(@Parameter(description = "商店的唯一ID") @PathVariable Long id, @RequestBody AdminDtos.StoreDto storeDto) {
        adminStoreService.updateStore(id, storeDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "删除指定ID的商店")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@Parameter(description = "商店的唯一ID") @PathVariable Long id) {
        adminStoreService.deleteStoreAndLocation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "分页查询商店列表")
    @GetMapping
    public ResponseEntity<Page<Store>> getStoreList(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量") @RequestParam(defaultValue = "10") long size
    ) {
        Page<Store> page = adminStoreService.page(new Page<>(current, size));
        return ResponseEntity.ok(page);
    }
}