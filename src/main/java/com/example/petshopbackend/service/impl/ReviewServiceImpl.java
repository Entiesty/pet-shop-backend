package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petshopbackend.dto.ReviewDtos;
import com.example.petshopbackend.entity.*;
import com.example.petshopbackend.mapper.*;
import com.example.petshopbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Override
    @Transactional
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
        baseMapper.insert(review);

        // --- 更新商品的平均分和评价总数 ---
        updateProductRatingStats(createDto.getProductId());
    }

    @Transactional
    public void updateProductRatingStats(Long productId) {
        // 查询该商品的所有评分
        List<Review> reviews = baseMapper.selectList(new LambdaQueryWrapper<Review>().eq(Review::getProductId, productId));
        if (reviews.isEmpty()) return;

        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        BigDecimal finalRating = BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
        int reviewCount = reviews.size();

        Product product = new Product();
        product.setId(productId);
        product.setAverageRating(finalRating);
        product.setReviewCount(reviewCount);
        productMapper.updateById(product);
    }

    @Override
    public Page<ReviewDtos.ReviewViewDto> getReviewsForProduct(Long productId, Page<Object> pageRequest) {
        Page<Review> reviewPage = baseMapper.selectPage(
                new Page<>(pageRequest.getCurrent(), pageRequest.getSize()),
                new LambdaQueryWrapper<Review>().eq(Review::getProductId, productId).orderByDesc(Review::getCreatedAt)
        );

        Page<ReviewDtos.ReviewViewDto> dtoPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        if (reviewPage.getRecords().isEmpty()) return dtoPage;

        // 批量获取评价的用户信息
        List<Long> userIds = reviewPage.getRecords().stream().map(Review::getUserId).distinct().toList();
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 组装DTO
        List<ReviewDtos.ReviewViewDto> dtoList = reviewPage.getRecords().stream().map(review -> {
            ReviewDtos.ReviewViewDto dto = new ReviewDtos.ReviewViewDto();
            User user = userMap.get(review.getUserId());
            dto.setContent(review.getContent());
            dto.setRating(review.getRating());
            dto.setCreatedAt(review.getCreatedAt());
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
