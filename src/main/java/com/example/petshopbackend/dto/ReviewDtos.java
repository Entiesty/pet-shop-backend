package com.example.petshopbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "评价相关的数据传输对象")
public class ReviewDtos {

    @Data
    @Schema(description = "创建新评价的请求体")
    public static class ReviewCreateDto {
        @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long orderId;
        @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long productId;
        @Schema(description = "评分 (1-5)", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer rating;
        @Schema(description = "评价内容")
        private String content;
    }

    @Data
    @Schema(description = "用于前端展示的评价视图对象")
    public static class ReviewViewDto {
        @Schema(description = "评价内容")
        private String content;
        @Schema(description = "评分")
        private Integer rating;
        @Schema(description = "评价时间")
        private LocalDateTime createdAt;
        @Schema(description = "评价用户的昵称")
        private String userNickname;
        @Schema(description = "评价用户的头像")
        private String userAvatarUrl;
    }
}
