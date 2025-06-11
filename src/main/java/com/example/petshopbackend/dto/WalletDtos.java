package com.example.petshopbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "钱包相关的数据传输对象")
public class WalletDtos {

    @Data
    @Schema(description = "充值请求体")
    public static class RechargeDto {
        @Schema(description = "充值金额", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal amount;
    }
}
