package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户个人信息模块", description = "查询当前登录用户的个人资料")
@RestController
@RequestMapping("/api/user/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前登录用户的个人信息", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<UserDtos.UserProfileDto> getCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        UserDtos.UserProfileDto userProfile = userService.getUserProfileByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userProfile);
    }

    /**
     * [ADDED] 更新当前登录用户的头像
     */
    @Operation(summary = "更新我的头像", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/avatar")
    public ResponseEntity<Void> updateUserAvatar(
            @RequestBody UserDtos.AvatarUpdateDto avatarDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.updateUserAvatar(userDetails.getUsername(), avatarDto.getAvatarUrl());
        return ResponseEntity.ok().build();
    }
}