package com.example.petshopbackend.service;

import com.example.petshopbackend.dto.AuthDtos;

public interface AuthService {
    String register(AuthDtos.RegisterDto registerDto);
    String login(AuthDtos.LoginDto loginDto);
    void logout(String username);
}
