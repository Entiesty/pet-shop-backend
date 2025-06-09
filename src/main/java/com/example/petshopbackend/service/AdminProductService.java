package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Product;

public interface AdminProductService extends IService<Product> {

    /**
     * 分页并根据条件查询商品列表
     * @param page 分页对象
     * @param storeId 商店ID (可选)
     * @param name 商品名称 (可选, 模糊查询)
     * @return 商品分页结果
     */
    Page<Product> listProducts(Page<Product> page, Long storeId, String name);

    /**
     * 创建新商品
     * @param productDto 商品数据
     */
    void createProduct(AdminDtos.ProductDto productDto);

    /**
     * 更新商品信息
     * @param id 商品ID
     * @param productDto 商品数据
     */
    void updateProduct(Long id, AdminDtos.ProductDto productDto);
}
