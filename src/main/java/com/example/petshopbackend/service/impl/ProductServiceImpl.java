package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.PriceDtos;
import com.example.petshopbackend.dto.ProductDtos;
import com.example.petshopbackend.dto.ReviewDtos;
import com.example.petshopbackend.entity.Category;
import com.example.petshopbackend.entity.Product;
import com.example.petshopbackend.entity.Store;
import com.example.petshopbackend.mapper.CategoryMapper;
import com.example.petshopbackend.mapper.ProductMapper;
import com.example.petshopbackend.mapper.StoreMapper;
import com.example.petshopbackend.service.ProductService;
import com.example.petshopbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final StoreMapper storeMapper;
    private final ReviewService reviewService;
    private final CategoryMapper categoryMapper; // 注入CategoryMapper

    @Override
    public Page<ProductDtos.ProductListViewDto> listProducts(Page<Product> page, Long storeId, String name, Long categoryId) {
        // 1. 先进行商品的基础分页查询
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(storeId != null, Product::getStoreId, storeId)
                .like(StringUtils.hasText(name), Product::getName, name)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .orderByDesc(Product::getCreatedAt);
        Page<Product> productPage = baseMapper.selectPage(page, wrapper);

        // 2. 获取所有分类信息，用于判断
        List<Category> allCategories = categoryMapper.selectList(null);
        Map<Long, Category> categoryMap = allCategories.stream().collect(Collectors.toMap(Category::getId, Function.identity()));

        // 3. 将Page<Product>转换为Page<ProductListViewDto>，并填充isPet字段
        Page<ProductDtos.ProductListViewDto> dtoPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());

        List<ProductDtos.ProductListViewDto> dtoList = productPage.getRecords().stream().map(product -> {
            ProductDtos.ProductListViewDto dto = new ProductDtos.ProductListViewDto();
            // 复制所有Product的属性到DTO
            BeanUtils.copyProperties(product, dto);
            // 判断isPet
            dto.setPet(isPetCategory(product.getCategoryId(), categoryMap));
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    /**
     * 递归判断一个分类是否属于“活体宠物”（ID为1）的子孙分类
     */
    private boolean isPetCategory(Long categoryId, Map<Long, Category> categoryMap) {
        if (categoryId == null) {
            return false;
        }
        if (categoryId == 1L) { // 顶级“活体宠物”分类的ID
            return true;
        }
        Category current = categoryMap.get(categoryId);
        if (current == null || current.getParentId() == 0) {
            return false; // 找不到分类或已经到顶级但不是“活体宠物”
        }
        // 递归向上查找父分类
        return isPetCategory(current.getParentId(), categoryMap);
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

    /**
     * [ADDED] 实现模糊搜索逻辑
     */
    @Override
    public Page<Product> searchProducts(String keyword, Page<Product> page) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 添加空值检查
        if (StringUtils.hasText(keyword)) {
            // 使用 OR 连接，同时对商品名称和描述进行模糊搜索
            wrapper.like(Product::getName, keyword)
                    .or()
                    .like(Product::getDescription, keyword);
        }

        try {
            wrapper.orderByDesc(Product::getCreatedAt);
            return baseMapper.selectPage(page, wrapper);
        } catch (Exception e) {
            log.error("搜索商品时发生错误", e);
            // 返回空页面
            return new Page<>(page.getCurrent(), page.getSize());
        }
    }

    @Override
    public PriceDtos.CalculatedPriceDto getCalculatedPrice(Long productId) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        BigDecimal originalPrice = product.getPrice();
        BigDecimal discount = product.getDiscount();
        // 计算最终价格，乘以折扣率，并四舍五入保留两位小数
        BigDecimal finalPrice = originalPrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
        // 判断是否在打折（折扣率不为1）
        boolean onSale = discount.compareTo(BigDecimal.ONE) != 0;

        return new PriceDtos.CalculatedPriceDto(originalPrice, discount, finalPrice, onSale);
    }
}
