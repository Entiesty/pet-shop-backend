package com.example.petshopbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
@Schema(description = "商品实体类")
public class Product {

    @TableId(type = IdType.AUTO)
    @Schema(description = "商品唯一ID")
    private Long id;

    @Schema(description = "所属商店ID")
    private Long storeId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "商品类型 (1-宠物, 2-周边)")
    private Integer productType;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "主图URL")
    private String mainImageUrl;

    @Schema(description = "介绍视频URL")
    private String videoUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    // [ADDED] 新增的评价统计字段
    @Schema(description = "平均评分")
    private BigDecimal averageRating;

    // [ADDED] 新增的评价统计字段
    @Schema(description = "评价总数")
    private Integer reviewCount;
}