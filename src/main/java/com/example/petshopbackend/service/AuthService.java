package com.example.petshopbackend.service;

import com.example.petshopbackend.dto.AuthDtos;

public interface AuthService {
    String register(AuthDtos.RegisterDto registerDto);
    // [MODIFIED] 将返回类型从 String 修改为 JwtAuthResponse
    AuthDtos.JwtAuthResponse login(AuthDtos.LoginDto loginDto);
    void logout(String username);
}
