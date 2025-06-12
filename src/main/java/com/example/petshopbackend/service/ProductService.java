package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.entity.Product;

public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表，支持多种筛选条件
     */
    Page<Product> listProducts(Page<Product> page, Long storeId, String name, Long categoryId, Integer productType);

    ProductDtos.ProductDetailViewDto getProductDetail(Long productId);
}
