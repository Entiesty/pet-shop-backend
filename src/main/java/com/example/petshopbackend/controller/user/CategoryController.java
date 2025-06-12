package com.example.petshopbackend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.petshopbackend.entity.Category;
import com.example.petshopbackend.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品分类模块", description = "提供查询商品分类信息的功能")
@RestController
@RequestMapping("/api/user/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @Operation(summary = "获取所有分类列表", description = "公开接口，用于前端生成导航菜单")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam(required = false) Long parentId) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        
        if (parentId != null) {
            // 获取指定父分类下的子分类
            queryWrapper.eq("parent_id", parentId);
        }
        
        queryWrapper.orderByAsc("sort_order", "id");
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "根据ID获取分类详情", description = "获取指定分类的详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }
}
