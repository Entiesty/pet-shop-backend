package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.ReviewDtos;
import com.example.petshopbackend.entity.*;
import com.example.petshopbackend.mapper.*;
import com.example.petshopbackend.service.ReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Override
    @Transactional
    public void createReview(ReviewDtos.ReviewCreateDto createDto, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // [MODIFIED] 移除了所有关于订单的校验
        // 只校验用户是否重复评价
        boolean hasReviewed = baseMapper.exists(new LambdaQueryWrapper<Review>()
                .eq(Review::getProductId, createDto.getProductId())
                .eq(Review::getUserId, user.getId()));
        if (hasReviewed) {
            throw new RuntimeException("您已经评价过此商品");
        }

        // 保存评价 (不再保存orderId)
        Review review = new Review();
        review.setUserId(user.getId());
        review.setProductId(createDto.getProductId());
        review.setRating(createDto.getRating());
        review.setContent(createDto.getContent());

        if (createDto.getImageUrls() != null && !createDto.getImageUrls().isEmpty()) {
            try {
                review.setImageUrls(objectMapper.writeValueAsString(createDto.getImageUrls()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片URL序列化失败", e);
            }
        }

        baseMapper.insert(review);

        // 更新商品的平均分和评价总数 (逻辑不变)
        updateProductRatingStats(createDto.getProductId());
    }

    @Transactional
    public void updateProductRatingStats(Long productId) {
        List<Review> reviews = baseMapper.selectList(new LambdaQueryWrapper<Review>().eq(Review::getProductId, productId));
        if (reviews.isEmpty()) return;

        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        BigDecimal finalRating = BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
        int reviewCount = reviews.size();

        Product productToUpdate = new Product();
        productToUpdate.setId(productId);
        productToUpdate.setAverageRating(finalRating);
        productToUpdate.setReviewCount(reviewCount);

        productMapper.updateById(productToUpdate);
    }

    @Override
    public Page<ReviewDtos.ReviewViewDto> getReviewsForProduct(Long productId, Page<Object> pageRequest) {
        // 1. 分页查询评价主信息
        Page<Review> reviewPage = baseMapper.selectPage(
                new Page<>(pageRequest.getCurrent(), pageRequest.getSize()),
                new LambdaQueryWrapper<Review>().eq(Review::getProductId, productId).orderByDesc(Review::getCreatedAt)
        );

        // 2. 准备用于返回的DTO分页对象
        Page<ReviewDtos.ReviewViewDto> dtoPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        if (reviewPage.getRecords().isEmpty()) {
            dtoPage.setRecords(Collections.emptyList());
            return dtoPage; // 如果没有评价，直接返回空的分页结果
        }

        // 3. 批量获取所有评价相关的用户信息
        List<Long> userIds = reviewPage.getRecords().stream().map(Review::getUserId).distinct().toList();
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 4. 将Review实体列表转换为ReviewViewDto列表
        List<ReviewDtos.ReviewViewDto> dtoList = reviewPage.getRecords().stream().map(review -> {
            ReviewDtos.ReviewViewDto dto = new ReviewDtos.ReviewViewDto();
            User user = userMap.get(review.getUserId());

            dto.setContent(review.getContent());
            dto.setRating(review.getRating());
            dto.setCreatedAt(review.getCreatedAt());

            // 将JSON字符串反序列化为图片URL列表
            if (StringUtils.hasText(review.getImageUrls())) {
                try {
                    dto.setImageUrls(objectMapper.readValue(review.getImageUrls(), new TypeReference<List<String>>() {}));
                } catch (JsonProcessingException e) {
                    log.error("评价图片URL反序列化失败, reviewId: {}", review.getId(), e);
                    dto.setImageUrls(Collections.emptyList()); // 出错时返回空列表
                }
            }

            // 填充用户信息
            if (user != null) {
                dto.setUserNickname(user.getNickname());
                dto.setUserAvatarUrl(user.getAvatarUrl());
            }
            return dto;
        }).toList();

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
}