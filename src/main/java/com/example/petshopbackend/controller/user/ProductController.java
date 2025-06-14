package com.example.petshopbackend.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petshopbackend.dto.PriceDtos;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户商品模块", description = "提供面向所有用户的商品查询功能")
@RestController
@RequestMapping("/api/user/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * [最终版] API接口只接收 categoryId 作为分类参数
     */
    @Operation(summary = "分页查询商品列表", description = "公开接口，可按商店、名称、分类等筛选")
    @GetMapping
    public ResponseEntity<Page<ProductDtos.ProductListViewDto>> getProductList(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "所属商店ID (可选)") @RequestParam(required = false) Long storeId,
            @Parameter(description = "商品名称 (可选, 模糊查询)") @RequestParam(required = false) String name,
            @Parameter(description = "分类ID (可选)") @RequestParam(required = false) Long categoryId
    ) {
        Page<ProductDtos.ProductListViewDto> page = productService.listProducts(new Page<>(current, size), storeId, name, categoryId);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "获取单个商品详情", description = "公开接口，返回商品、商店和评价的聚合信息")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDtos.ProductDetailViewDto> getProductDetail(
            @Parameter(description = "商品ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }

    /**
     * [ADDED] 商品模糊搜索接口
     */
    @Operation(summary = "根据关键词搜索商品", description = "公开接口，对商品名称和描述进行模糊搜索")
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页显示数量") @RequestParam(defaultValue = "10") long size
    ) {
        Page<Product> page = productService.searchProducts(keyword, new Page<>(current, size));
        return ResponseEntity.ok(page);
    }
    //商品折扣
    @Operation(summary = "获取商品的计算后价格", description = "公开接口，根据商品ID返回其原价、折扣和最终售价")
    @GetMapping("/{id}/price")
    public ResponseEntity<PriceDtos.CalculatedPriceDto> getProductPrice(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getCalculatedPrice(id));
    }
}