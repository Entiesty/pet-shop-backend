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
    public Page<Product> listProducts(Page<Product> page, Long storeId, String name) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 如果传入storeId，则作为查询条件
        if (storeId != null) {
            wrapper.eq(Product::getStoreId, storeId);
        }
        // 如果传入name，则作为模糊查询条件
        if (StringUtils.hasText(name)) {
            wrapper.like(Product::getName, name);
        }
        wrapper.orderByDesc(Product::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public void createProduct(AdminDtos.ProductDto productDto) {
        // 校验所属商店是否存在
        Store store = storeMapper.selectById(productDto.getStoreId());
        if (store == null) {
            throw new RuntimeException("指定的商店不存在");
        }

        Product product = new Product();
        // 此处可以使用MapStruct等工具进行转换，为简化手动设置
        product.setStoreId(productDto.getStoreId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setProductType(productDto.getProductType());
        product.setStock(productDto.getStock());
        product.setMainImageUrl(productDto.getMainImageUrl());
        product.setVideoUrl(productDto.getVideoUrl());

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

        product.setStoreId(productDto.getStoreId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setProductType(productDto.getProductType());
        product.setStock(productDto.getStock());
        product.setMainImageUrl(productDto.getMainImageUrl());
        product.setVideoUrl(productDto.getVideoUrl());

        baseMapper.updateById(product);
    }
}
