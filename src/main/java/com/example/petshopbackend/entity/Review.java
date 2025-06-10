package com.example.petshopbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reviews")
public class Review {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderId;
    private Integer rating;
    private String content;
    // 新增字段，用于存储图片URL的JSON数组字符串
    private String imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}