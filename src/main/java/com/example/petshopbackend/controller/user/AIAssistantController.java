package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.AIDtos;
import com.example.petshopbackend.service.AIAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI智能助手模块", description = "提供与大模型交互的问答功能")
@RestController
@RequestMapping("/api/user/ai")
@RequiredArgsConstructor
public class AIAssistantController {

    private final AIAssistantService aiAssistantService;

    @Operation(
            summary = "向AI助手提问",
            description = "将用户输入的问题发送给AI助手，返回AI生成的回答",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @RequestBody(
                    required = true,
                    description = "包含用户问题的请求体",
                    content = @Content(schema = @Schema(implementation = AIDtos.ChatRequest.class))
            )
    )
    @PostMapping("/chat")
    public ResponseEntity<AIDtos.ChatResponse> chatWithAssistant(@RequestBody AIDtos.ChatRequest request) {
        try {
            String reply = aiAssistantService.getReply(request.getQuestion());
            return ResponseEntity.ok(new AIDtos.ChatResponse(reply, true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AIDtos.ChatResponse("AI服务暂时不可用: " + e.getMessage(), false));
        }
    }
}
