package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.AuthDtos;
import com.example.petshopbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户认证模块", description = "提供用户注册、登录、登出等公开接口")
@RestController
@RequestMapping("/api/user/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthDtos.RegisterDto registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * [MODIFIED] 修改登录接口以返回新的DTO结构
     */
    @Operation(summary = "用户登录", description = "登录成功后返回JWT访问令牌和用户角色")
    @PostMapping("/login")
    public ResponseEntity<AuthDtos.JwtAuthResponse> login(@RequestBody AuthDtos.LoginDto loginDto) {
        // 现在service直接返回完整的DTO对象，我们直接将其返回即可
        AuthDtos.JwtAuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "用户登出", description = "需要携带有效的Token来访问此接口，用于让当前Token失效")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            authService.logout(userDetails.getUsername());
        }
        return ResponseEntity.ok("登出成功");
    }

    @Operation(summary = "发送邮箱登录验证码")
    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody AuthDtos.EmailRequestDto requestDto) {
        authService.sendLoginCode(requestDto.email());
        return ResponseEntity.ok("验证码发送成功，请检查您的邮箱。");
    }

    @Operation(summary = "通过邮箱验证码登录")
    @PostMapping("/login-by-code")
    public ResponseEntity<AuthDtos.JwtAuthResponse> loginByCode(@RequestBody AuthDtos.EmailLoginDto loginDto) {
        return ResponseEntity.ok(authService.loginByCode(loginDto));
    }
}