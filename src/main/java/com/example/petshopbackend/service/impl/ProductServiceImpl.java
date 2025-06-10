package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.dto.ReviewDtos;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.mapper.ProductMapper;
import com.example.petshopbackend.mapper.StoreMapper;
import com.example.petshopbackend.service.ProductService;
import com.example.petshopbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final StoreMapper storeMapper;
    private final ReviewService reviewService;

    @Override
    public Page<Product> listProducts(Page<Product> page, Long storeId, String name, Integer productType) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(storeId != null, Product::getStoreId, storeId)
                .like(StringUtils.hasText(name), Product::getName, name)
                .eq(productType != null, Product::getProductType, productType)
                .orderByDesc(Product::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public ProductDtos.ProductDetailViewDto getProductDetail(Long productId) {
        // 1. 获取商品基本信息
        Product product = baseMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 获取所属商店信息
        Store store = storeMapper.selectById(product.getStoreId());

        // 3. 获取第一页的评价信息
        Page<ReviewDtos.ReviewViewDto> reviews = reviewService.getReviewsForProduct(productId, new Page<>(1, 5));

        // 4. 组装成DTO
        ProductDtos.ProductDetailViewDto dto = new ProductDtos.ProductDetailViewDto();
        dto.setProduct(product);
        dto.setStore(store);
        dto.setReviews(reviews);

        return dto;
    }
}
