package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.entity.Product;

public interface ProductService extends IService<Product> {

    /**
     * (公开) 分页并按条件查询商品列表
     */
    Page<Product> listProducts(Page<Product> page, Long storeId, String name, Integer productType);

    /**
     * (公开) 获取商品详情，聚合商店和评价信息
     */
    ProductDtos.ProductDetailViewDto getProductDetail(Long productId);
}
