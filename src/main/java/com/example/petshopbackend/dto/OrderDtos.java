package com.example.petshopbackend.dto;

import com.example.petshopbackend.entity.Order;
import com.example.petshopbackend.entity.OrderItem;
import com.example.petshopbackend.entity.Product;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    /**
     * 用于接收前端“创建订单”请求的DTO
     */
    @Data
    public static class OrderCreateDto {
        private Long addressId; // 用户选择的收货地址ID
        // 用户在购物车中勾选的、要下单的购物车项ID列表
        private List<Long> cartItemIds;
        private String remark; // 订单备注
    }

    /**
     * 用于向前端“展示订单详情”的DTO
     */
    @Data
    public static class OrderViewDto {
        private Long id;
        private String orderNo;
        private BigDecimal totalAmount;
        private Integer status;
        private LocalDateTime createdAt;
        private String contactName;
        private String phone;
        private String fullAddress;
        private List<OrderItemView> items;
    }

    /**
     * 订单中单个商品的视图对象
     */
    @Data
    public static class OrderItemView {
        private Long productId;
        private String name;
        private String mainImageUrl;
        private BigDecimal unitPrice; // 下单时的单价
        private Integer quantity;
    }
}
