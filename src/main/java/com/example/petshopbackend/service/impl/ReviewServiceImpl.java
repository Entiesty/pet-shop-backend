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
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper; // 注入Jackson的核心工具，用于JSON序列化/反序列化

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);


    @Override
    @Transactional(rollbackFor = Exception.class) // 保证整个方法是事务性的
    public void createReview(ReviewDtos.ReviewCreateDto createDto, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Order order = orderMapper.selectById(createDto.getOrderId());

        // --- 核心业务校验 ---
        if (order == null || !order.getUserId().equals(user.getId())) {
            throw new RuntimeException("订单不存在或无权评价");
        }
        // 假设40是“已完成”状态，用户只能评价已完成的订单
        if (order.getStatus() != 40) {
            throw new RuntimeException("只有已完成的订单才能评价");
        }
        boolean productInOrder = orderItemMapper.exists(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId())
                .eq(OrderItem::getProductId, createDto.getProductId()));
        if (!productInOrder) {
            throw new RuntimeException("您未购买过此商品，无法评价");
        }
        boolean hasReviewed = baseMapper.exists(new LambdaQueryWrapper<Review>()
                .eq(Review::getOrderId, order.getId())
                .eq(Review::getProductId, createDto.getProductId())
                .eq(Review::getUserId, user.getId()));
        if (hasReviewed) {
            throw new RuntimeException("您已经评价过此商品");
        }

        // --- 保存评价 ---
        Review review = new Review();
        review.setUserId(user.getId());
        review.setOrderId(createDto.getOrderId());
        review.setProductId(createDto.getProductId());
        review.setRating(createDto.getRating());
        review.setContent(createDto.getContent());

        // 将图片URL列表转换为JSON字符串进行存储
        if (createDto.getImageUrls() != null && !createDto.getImageUrls().isEmpty()) {
            try {
                review.setImageUrls(objectMapper.writeValueAsString(createDto.getImageUrls()));
            } catch (JsonProcessingException e) {
                // 在真实项目中，这里应该有更完善的异常处理
                log.error("评价图片URL序列化失败", e);
                throw new RuntimeException("图片URL格式错误");
            }
        }

        baseMapper.insert(review);

        // --- 更新商品的平均分和评价总数 ---
        updateProductRatingStats(createDto.getProductId());
    }

    /**
     * 更新商品的评价统计信息
     * @param productId 商品ID
     */
    @Transactional
    public void updateProductRatingStats(Long productId) {
        // 查询该商品的所有评分
        List<Review> reviews = baseMapper.selectList(new LambdaQueryWrapper<Review>().eq(Review::getProductId, productId));
        if (reviews.isEmpty()) {
            return; // 如果没有评价，则不做任何操作
        }

        // 计算平均分
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        BigDecimal finalRating = BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP); // 四舍五入保留一位小数
        // 计算评价总数
        int reviewCount = reviews.size();

        // 创建一个只包含待更新字段的Product对象
        Product productToUpdate = new Product();
        productToUpdate.setId(productId);
        productToUpdate.setAverageRating(finalRating);
        productToUpdate.setReviewCount(reviewCount);

        // MyBatis-Plus的updateById会智能地只更新非null字段
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