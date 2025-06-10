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

    @Override
    public Page<Product> listProducts(Page<Product> page, Long storeId, String name, Integer productType) {
        try {
            log.info("正在查询商品列表: page={}, storeId={}, name={}, productType={}", 
                    page.getCurrent(), storeId, name, productType);
            
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(storeId != null, Product::getStoreId, storeId)
                    .like(StringUtils.hasText(name), Product::getName, name)
                    .eq(productType != null, Product::getProductType, productType)
                    .orderByDesc(Product::getCreatedAt);
            
            Page<Product> result = baseMapper.selectPage(page, wrapper);
            log.info("查询结果: 总数={}, 当前页数据量={}", result.getTotal(), result.getRecords().size());
            
            return result;
        } catch (Exception e) {
            log.error("查询商品列表时发生错误", e);
            throw new RuntimeException("查询商品列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductDtos.ProductDetailViewDto getProductDetail(Long productId) {
        try {
            log.info("正在查询商品详情: productId={}", productId);
            
            // 1. 获取商品基本信息
            Product product = baseMapper.selectById(productId);
            if (product == null) {
                log.warn("未找到商品: productId={}", productId);
                throw new RuntimeException("商品不存在");
            }
            log.info("找到商品: {}", product.getName());

            // 2. 获取所属商店信息
            Store store = storeMapper.selectById(product.getStoreId());
            if (store == null) {
                log.warn("商品所属商店不存在: storeId={}", product.getStoreId());
            } else {
                log.info("找到商店: {}", store.getName());
            }

            // 3. 获取第一页的评价信息
            Page<ReviewDtos.ReviewViewDto> reviews;
            try {
                reviews = reviewService.getReviewsForProduct(productId, new Page<>(1, 5));
                log.info("获取到评价数: {}", reviews.getRecords().size());
            } catch (Exception e) {
                log.error("获取商品评价失败", e);
                reviews = new Page<>();
            }

            // 4. 组装成DTO
            ProductDtos.ProductDetailViewDto dto = new ProductDtos.ProductDetailViewDto();
            dto.setProduct(product);
            dto.setStore(store);
            dto.setReviews(reviews);

            return dto;
        } catch (Exception e) {
            log.error("获取商品详情时发生错误: productId=" + productId, e);
            throw new RuntimeException("获取商品详情失败: " + e.getMessage(), e);
        }
    }
}
