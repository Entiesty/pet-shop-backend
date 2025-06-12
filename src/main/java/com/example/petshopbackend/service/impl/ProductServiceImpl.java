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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final StoreMapper storeMapper;
    private final ReviewService reviewService;

    /**
     * [最终版] 查询逻辑中只使用 categoryId
     */
    @Override
    public Page<Product> listProducts(Page<Product> page, Long storeId, String name, Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(storeId != null, Product::getStoreId, storeId)
                .like(StringUtils.hasText(name), Product::getName, name)
                .eq(categoryId != null, Product::getCategoryId, categoryId) // 使用 categoryId 进行筛选
                .orderByDesc(Product::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public ProductDtos.ProductDetailViewDto getProductDetail(Long productId) {
        // 1. 获取商品基本信息 (不变)
        Product product = this.getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 获取所属商店信息 (不变)
        Store store = storeMapper.selectById(product.getStoreId());

        // 3. 获取第一页的评价信息 (不变)
        Page<ReviewDtos.ReviewViewDto> reviews = reviewService.getReviewsForProduct(productId, new Page<>(1, 5));

        // 4. [MODIFIED] 手动组装成“扁平化”的DTO
        ProductDtos.ProductDetailViewDto dto = new ProductDtos.ProductDetailViewDto();

        // 填充商品信息
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setMainImageUrl(product.getMainImageUrl());
        dto.setVideoUrl(product.getVideoUrl());
        dto.setStock(product.getStock());
        dto.setDescription(product.getDescription());
        dto.setBreed(product.getBreed());
        dto.setAge(product.getAge());
        dto.setSex(product.getSex());
        dto.setWeight(product.getWeight());
        dto.setColor(product.getColor());
        dto.setHealthInfo(product.getHealthInfo());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());

        // 填充商店信息
        if (store != null) {
            ProductDtos.ProductDetailViewDto.StoreInfo storeInfo = new ProductDtos.ProductDetailViewDto.StoreInfo();
            storeInfo.setId(store.getId());
            storeInfo.setName(store.getName());
            storeInfo.setLogoUrl(store.getLogoUrl());
            dto.setStore(storeInfo);
        }

        // 填充评价信息
        dto.setReviews(reviews);

        return dto;
    }
}
