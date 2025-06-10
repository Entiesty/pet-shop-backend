package com.example.petshopbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petshopbackend.dto.ReviewDtos;
import com.example.petshopbackend.entity.Review;

public interface ReviewService extends IService<Review> {

    /**
     * 创建一条新评价
     */
    void createReview(ReviewDtos.ReviewCreateDto createDto, String username);

    /**
     * 分页查询某个商品的评价列表
     */
    Page<ReviewDtos.ReviewViewDto> getReviewsForProduct(Long productId, Page<Object> page);
}
