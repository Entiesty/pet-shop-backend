package com.example.petshopbackend.service.impl;

import com.example.petshopbackend.service.AIAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIAssistantServiceImpl implements AIAssistantService {

    private final ChatClient chatClient;

    @Override
    public String getReply(String userQuestion) {
        String finalPrompt = "你是一个名为“萌宠之家”的宠物商店的智能助手。" +
                "请友好、专业、简洁地回答以下关于宠物的问题。如果问题与宠物完全无关，请礼貌地拒绝回答。\n\n" +
                "用户问题是：\"" + userQuestion + "\"";

        return chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();
    }
}