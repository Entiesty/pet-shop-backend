package com.example.petshopbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "订单相关的DTOs")
public class OrderDtos {

    /**
     * 用于接收前端“创建订单”请求的DTO
     */
    @Data
    @Schema(description = "用于接收前端“创建订单”请求的DTO")
    public static class OrderCreateDto {
        @Schema(description = "用户选择的收货地址ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long addressId;

        @Schema(description = "要下单的购物车项ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        private List<Long> cartItemIds;

        @Schema(description = "订单备注", example = "请在工作日派送")
        private String remark;
    }

    /**
     * 用于向前端“展示订单详情”的DTO
     */
    @Data
    @Schema(description = "用于向前端“展示订单详情”的DTO")
    public static class OrderViewDto {
        @Schema(description = "订单ID", example = "10001")
        private Long id;

        @Schema(description = "订单编号", example = "20240520112233001")
        private String orderNo;

        @Schema(description = "订单总金额", example = "399.00")
        private BigDecimal totalAmount;

        @Schema(description = "订单状态", example = "1") // 可以进一步说明：0-待付款, 1-待发货, 2-已发货, 3-已完成, 4-已取消
        private Integer status;

        @Schema(description = "下单时间")
        private LocalDateTime createdAt;

        @Schema(description = "收货人姓名", example = "王先生")
        private String contactName;

        @Schema(description = "收货人电话", example = "13812345678")
        private String phone;

        @Schema(description = "完整收货地址", example = "上海市浦东新区张江高科XX路XX号")
        private String fullAddress;

        @Schema(description = "订单商品列表")
        private List<OrderItemView> items;
    }

    /**
     * 订单中单个商品的视图对象
     */
    @Data
    @Schema(description = "订单中单个商品的视图对象")
    public static class OrderItemView {
        @Schema(description = "商品ID", example = "1")
        private Long productId;

        @Schema(description = "商品名称", example = "天然无谷物狗粮")
        private String name;

        @Schema(description = "商品主图URL", example = "http://example.com/images/dogfood.jpg")
        private String mainImageUrl;

        @Schema(description = "下单时的商品单价", example = "199.50")
        private BigDecimal unitPrice;

        @Schema(description = "购买数量", example = "2")
        private Integer quantity;
    }
}