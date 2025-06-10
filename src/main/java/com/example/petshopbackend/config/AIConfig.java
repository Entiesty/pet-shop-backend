package com.example.petshopbackend.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Bean
    public ChatClient chatClient() {
        // 1. 创建 OpenAiApi 实例（需传入 apiKey）
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);

        // 2. 配置模型参数
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .build();

        // 3. 创建 OpenAiChatModel
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);

        // 4. 返回 ChatClient
        return ChatClient.builder(chatModel)
                .build();
    }
}