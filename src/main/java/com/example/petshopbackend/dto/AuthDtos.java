package com.example.petshopbackend.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * 用于存放所有与认证相关的DTO的容器类。
 * 我们使用Java的 record 关键字来简洁地定义这些不可变的数据对象。
 */
public class AuthDtos {

    /**
     * 用于接收前端“注册”请求的DTO。
     * @param username 用户名，不能为空
     * @param password 密码，不能为空
     * @param nickname 昵称（可选）
     */
    public record RegisterDto(
            @NotEmpty(message = "用户名不能为空") String username,
            @NotEmpty(message = "密码不能为空") String password,
            String nickname
    ) {}

    /**
     * 用于接收前端“登录”请求的DTO。
     * @param username 用户名
     * @param password 密码
     */
    public record LoginDto(
            @NotEmpty(message = "用户名不能为空") String username,
            @NotEmpty(message = "密码不能为空") String password
    ) {}

    /**
     * 用于向前端返回“登录”成功后的JWT响应的DTO。
     * @param accessToken 生成的JWT字符串
     */
    public record JwtAuthResponse(String accessToken) {}
}
