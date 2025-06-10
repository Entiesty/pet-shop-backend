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

    /**
     * [MODIFIED] 修改登录逻辑以返回Token和角色
     */
    @Override
    public AuthDtos.JwtAuthResponse login(AuthDtos.LoginDto loginDto) {
        // 1. 使用AuthenticationManager进行用户认证，逻辑不变
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );

        // 2. 从认证成功的结果中获取用户信息
        // authentication.getPrincipal() 返回的是我们在UserDetailsServiceImpl中加载的User对象
        User user = (User) authentication.getPrincipal();

        // 3. 使用用户名生成JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 4. 将token存入Redis，逻辑不变
        redisService.set(
                "login_token:" + user.getUsername(),
                token,
                jwtExpiration,
                TimeUnit.MILLISECONDS
        );

        // 5. 创建并返回包含Token和角色的新DTO
        return new AuthDtos.JwtAuthResponse(token, user.getRole());
    }

    @Override
    public void logout(String username) {
        redisService.delete("login_token:" + username);
    }
}
