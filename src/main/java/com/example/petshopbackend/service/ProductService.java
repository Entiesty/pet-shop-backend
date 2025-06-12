package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.entity.Product;

public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表，支持多种筛选条件
     */
    /**
     * [最终版] 只保留 categoryId 作为分类筛选参数
     */
    Page<Product> listProducts(Page<Product> page, Long storeId, String name, Long categoryId);
    ProductDtos.ProductDetailViewDto getProductDetail(Long productId);
    /**
     * [ADDED] 根据关键词模糊搜索商品（分页）
     * @param keyword 搜索关键词
     * @param page 分页对象
     * @return 商品分页结果
     */
    Page<Product> searchProducts(String keyword, Page<Product> page);
}
