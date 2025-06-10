package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.entity.Category;
import com.example.petshopbackend.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "商品分类模块", description = "提供查询商品分类信息的功能")
@RestController
@RequestMapping("/api/user/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @Operation(summary = "获取所有分类列表", description = "公开接口，用于前端生成导航菜单")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        // 简单实现，直接返回所有分类
        List<Category> categories = categoryMapper.selectList(null);
        return ResponseEntity.ok(categories);
    }
}
