package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.entity.Product;

public interface ProductService extends IService<Product> {

    /**
     * [MODIFIED] 将 productType 参数修改为 categoryId
     */
    Page<Product> listProducts(Page<Product> page, Long storeId, String name, Long categoryId);

    ProductDtos.ProductDetailViewDto getProductDetail(Long productId);
}
