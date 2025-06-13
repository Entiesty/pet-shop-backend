package com.example.petshopbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "价格相关的数据传输对象")
public class PriceDtos {

    @Data
    @AllArgsConstructor
    @Schema(description = "商品价格计算结果DTO")
    public static class CalculatedPriceDto {
        @Schema(description = "原价")
        private BigDecimal originalPrice;

        @Schema(description = "折扣率 (1.00代表无折扣)")
        private BigDecimal discount;

        @Schema(description = "折扣后的最终价格")
        private BigDecimal finalPrice;

        @Schema(description = "是否正在打折")
        private boolean onSale;
    }
}