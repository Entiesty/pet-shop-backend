package com.example.petshopbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "AI助手相关的数据传输对象")
public class AIDtos {

    @Data
    @Schema(description = "AI聊天请求体")
    public static class ChatRequest {
        @Schema(description = "用户提出的问题", requiredMode = Schema.RequiredMode.REQUIRED)
        private String question;
    }

    @Data
    @NoArgsConstructor
    @Schema(description = "AI聊天响应体")
    public static class ChatResponse {
        @Schema(description = "AI生成的回答 或 错误信息")
        private String reply;
        @Schema(description = "请求是否成功")
        private boolean success;

        public ChatResponse(String reply, boolean success) {
            this.reply = reply;
            this.success = success;
        }
    }
}