package com.example.petshopbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "用户认证相关的数据传输对象")
public class AuthDtos {

    @Schema(description = "用户注册请求体")
    public record RegisterDto(
            @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotEmpty(message = "用户名不能为空") String username,
            @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotEmpty(message = "密码不能为空") String password,
            @Schema(description = "昵称")
            String nickname
    ) {}

    @Schema(description = "用户登录请求体")
    public record LoginDto(
            @Schema(description = "用户名")
            @NotEmpty(message = "用户名不能为空") String username,
            @Schema(description = "密码")
            @NotEmpty(message = "密码不能为空") String password
    ) {}

    @Schema(description = "JWT认证成功响应体")
    public record JwtAuthResponse(
            @Schema(description = "访问令牌 (Access Token)")
            String accessToken,

            @Schema(description = "用户角色 (0-会员, 1-管理员)")
            Integer role
    ) {}
}