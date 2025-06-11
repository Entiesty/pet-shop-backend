package com.example.petshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.petshopbackend.dto.AuthDtos;
import com.example.petshopbackend.entity.User;
import com.example.petshopbackend.mapper.UserMapper;
import com.example.petshopbackend.service.AuthService;
import com.example.petshopbackend.service.EmailService;
import com.example.petshopbackend.service.RedisService;
import com.example.petshopbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final EmailService emailService;

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

    @Override
    public void sendLoginCode(String email) {
        // 1. 生成一个6位的随机数字验证码
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        // 2. 将验证码存入Redis，并设置5分钟过期
        redisService.set("login_code:" + email, code, 5, TimeUnit.MINUTES);
        // 3. 发送邮件
        emailService.sendVerificationCode(email, code);
    }

    @Override
    @Transactional
    public AuthDtos.JwtAuthResponse loginByCode(AuthDtos.EmailLoginDto loginDto) {
        // 1. 从Redis中获取正确的验证码
        String correctCode = redisService.get("login_code:" + loginDto.email());
        if (correctCode == null) {
            throw new RuntimeException("验证码已过期或不存在");
        }
        // 2. 校验用户输入的验证码
        if (!correctCode.equals(loginDto.code())) {
            throw new RuntimeException("验证码错误");
        }

        // 3. 验证通过后，删除Redis中的验证码，防止重复使用
        redisService.delete("login_code:" + loginDto.email());

        // 4. 根据邮箱查找用户，如果不存在则自动注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getEmail, loginDto.email());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            user = new User();
            user.setEmail(loginDto.email());
            // 用户名可以默认为邮箱前缀
            user.setUsername(loginDto.email().split("@")[0] + "_" + System.currentTimeMillis());
            // 密码可以设置为一个随机的强密码，因为用户不会用它登录
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setRole(0); // 默认为普通用户
            userMapper.insert(user);
        }

        // 5. 生成JWT并返回，完成登录
        String token = jwtUtil.generateToken(user.getUsername());
        redisService.set("login_token:" + user.getUsername(), token, jwtExpiration, TimeUnit.MILLISECONDS);

        return new AuthDtos.JwtAuthResponse(token, user.getRole());
    }
}
