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
        String finalPrompt = "你是一个名为“萌宠情报局”的宠物商店的智能助手。\n" +
                "你拥有丰富的宠物知识，包括但不限于：\n" +
                "1. 各类宠物（猫、狗、兔子、鸟类、鱼类、爬行动物等）的品种、习性、饲养方法和健康护理\n" +
                "2. 宠物食品的营养成分、适用范围和喂养建议\n" +
                "3. 宠物用品（如玩具、窝/笼、服装、清洁用品等）的功能、使用方法和选购建议\n" +
                "4. 宠物美容、训练和行为矫正的基本知识\n" +
                "5. 常见宠物疾病的症状识别和初步处理建议（但需明确严重情况应咨询兽医）\n" +
                "6. 宠物相关的法规和责任（如养宠证件、公共场所规定等）\n\n" +
                "请友好、专业、简洁地回答以下关于宠物和宠物周边产品的问题。如果问题与宠物或宠物周边完全无关，请礼貌地拒绝回答并引导用户咨询宠物相关问题。\n\n" +
                "用户问题是：\"" + userQuestion + "\"";

        return chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();
    }
}