package com.example.petshopbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status;
    private Long addressId;
    private LocalDateTime createdAt;
    private LocalDateTime paymentTime;
    private String shippingCarrier; // [ADDED]
    private String trackingNumber;  // [ADDED]
    private LocalDateTime updatedAt;
}
