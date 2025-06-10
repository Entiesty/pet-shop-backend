package com.example.petshopbackend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-商品管理模块", description = "提供对宠物及周边商品的增删改查等管理功能")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    // ... create, update, delete, getById 方法保持不变 ...

    @Operation(summary = "分页并按条件查询商品列表")
    @GetMapping
    public ResponseEntity<Page<Product>> getProductList(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "所属商店ID (可选, 用于筛选)") @RequestParam(required = false) Long storeId,
            @Parameter(description = "商品名称 (可选, 用于模糊查询)") @RequestParam(required = false) String name,
            @Parameter(description = "分类ID (可选, 用于筛选)") @RequestParam(required = false) Long categoryId // [MODIFIED]
    ) {
        Page<Product> page = adminProductService.listProducts(new Page<>(current, size), storeId, name, categoryId);
        return ResponseEntity.ok(page);
    }
}