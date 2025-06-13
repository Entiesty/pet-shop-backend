package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.entity.Product;

public interface ProductService extends IService<Product> {

    // [MODIFIED] 修改列表查询方法的返回类型
    Page<ProductDtos.ProductListViewDto> listProducts(Page<Product> page, Long storeId, String name, Long categoryId);

    ProductDtos.ProductDetailViewDto getProductDetail(Long productId);
    /**
     * [ADDED] 根据关键词模糊搜索商品（分页）
     * @param keyword 搜索关键词
     * @param page 分页对象
     * @return 商品分页结果
     */
    Page<Product> searchProducts(String keyword, Page<Product> page);
}
