package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.AdminDtos;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.mapper.ProductMapper;
import com.example.petshopbackend.mapper.StoreMapper;
import com.example.petshopbackend.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements AdminProductService {

    private final StoreMapper storeMapper;

    @Override
    public Page<Product> listProducts(Page<Product> page, Long storeId, String name, Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(storeId != null, Product::getStoreId, storeId)
                .like(StringUtils.hasText(name), Product::getName, name)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .orderByDesc(Product::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public void createProduct(AdminDtos.ProductDto productDto) {
        // 校验所属商店是否存在
        Store store = storeMapper.selectById(productDto.getStoreId());
        if (store == null) {
            throw new RuntimeException("指定的商店不存在");
        }

        Product product = mapDtoToEntity(productDto, new Product());
        baseMapper.insert(product);
    }

    @Override
    public void updateProduct(Long id, AdminDtos.ProductDto productDto) {
        Product product = baseMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 校验所属商店是否存在
        Store store = storeMapper.selectById(productDto.getStoreId());
        if (store == null) {
            throw new RuntimeException("指定的商店不存在");
        }

        Product updatedProduct = mapDtoToEntity(productDto, product);
        baseMapper.updateById(updatedProduct);
    }

    // 辅助方法，用于将DTO映射到实体
    private Product mapDtoToEntity(AdminDtos.ProductDto productDto, Product product) {
        product.setStoreId(productDto.getStoreId());
        product.setCategoryId(productDto.getCategoryId());
        product.setName(productDto.getName());
        product.setBreed(productDto.getBreed());
        product.setAge(productDto.getAge());
        product.setSex(productDto.getSex());
        product.setWeight(productDto.getWeight());
        product.setColor(productDto.getColor());
        product.setDescription(productDto.getDescription());
        product.setHealthInfo(productDto.getHealthInfo());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setMainImageUrl(productDto.getMainImageUrl());
        product.setVideoUrl(productDto.getVideoUrl());
        return product;
    }
}