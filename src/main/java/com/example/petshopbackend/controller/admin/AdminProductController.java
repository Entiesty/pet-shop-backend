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

    @Operation(summary = "新增商品", description = "创建一个新的商品记录")
    @PostMapping
    public ResponseEntity<Void> createProduct(
            @Parameter(description = "商品信息请求体", required = true)
            @RequestBody AdminDtos.ProductDto productDto) {
        adminProductService.createProduct(productDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "更新指定ID的商品信息", description = "根据商品ID更新商品的详细信息")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(
            @Parameter(description = "商品的唯一ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "更新后的商品信息", required = true)
            @RequestBody AdminDtos.ProductDto productDto) {
        adminProductService.updateProduct(id, productDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "删除指定ID的商品", description = "根据商品ID删除对应的商品记录")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "商品的唯一ID", required = true)
            @PathVariable Long id) {
        adminProductService.removeById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "根据ID获取商品详情", description = "根据商品ID获取商品的详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "商品的唯一ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(adminProductService.getById(id));
    }

    @Operation(summary = "分页并按条件查询商品列表", description = "支持分页查询商品信息，可按商店ID、名称、分类ID筛选")
    @GetMapping
    public ResponseEntity<Page<Product>> getProductList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量", example = "10")
            @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "所属商店ID (可选, 用于筛选)")
            @RequestParam(required = false) Long storeId,
            @Parameter(description = "商品名称 (可选, 用于模糊查询)")
            @RequestParam(required = false) String name,
            @Parameter(description = "分类ID (可选, 用于筛选)")
            @RequestParam(required = false) Long categoryId) {
        Page<Product> page = adminProductService.listProducts(new Page<>(current, size), storeId, name, categoryId);
        return ResponseEntity.ok(page);
    }
}
