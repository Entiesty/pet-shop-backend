package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Product;

public interface AdminProductService extends IService<Product> {

    Page<Product> listProducts(Page<Product> page, Long storeId, String name, Long categoryId);

    void createProduct(AdminDtos.ProductDto productDto);

    void updateProduct(Long id, AdminDtos.ProductDto productDto);
}
