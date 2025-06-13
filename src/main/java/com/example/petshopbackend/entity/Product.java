package com.example.petshopbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long storeId;
    private Long categoryId;
    // private Integer productType; // 商品类型 (1-活体宠物, 2-宠物用品)
    private String name;
    private String breed;
    private String age;
    private String sex;
    private BigDecimal weight;
    private String color;
    private String description;
    private String healthInfo;
    private BigDecimal price;
    private BigDecimal discount;
    private Integer stock;
    private String mainImageUrl;
    private String videoUrl;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}