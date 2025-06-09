package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.petshopbackend.dto.AuthDtos;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.AuthService;
import com.example.petshopbackend.service.RedisService;
import com.example.petshopbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Value("${jwt.expire}")
    private Long jwtExpiration;

    @Override
    public String register(AuthDtos.RegisterDto registerDto) {
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, registerDto.username())) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDto.username());
        user.setPassword(passwordEncoder.encode(registerDto.password()));
        user.setNickname(registerDto.nickname());
        user.setRole(0);

        userMapper.insert(user);
        return "注册成功";
    }

    @Override
    public String login(AuthDtos.LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );

        String token = jwtUtil.generateToken(loginDto.username());

        redisService.set(
                "login_token:" + loginDto.username(),
                token,
                jwtExpiration,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    @Override
    public void logout(String username) {
        redisService.delete("login_token:" + username);
    }
}
