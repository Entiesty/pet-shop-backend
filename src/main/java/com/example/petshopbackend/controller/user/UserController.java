package com.example.petshopbackend.controller.user;

import com.example.petshopbackend.dto.UserDtos;
import com.example.petshopbackend.service.FileStorageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@Tag(name = "用户个人信息模块", description = "查询与修改当前登录用户的个人资料")
@RestController
@RequestMapping("/api/user/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Operation(summary = "获取当前登录用户的个人信息", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<UserDtos.UserProfileDto> getCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        UserDtos.UserProfileDto userProfile = userService.getUserProfileByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userProfile);
    }

    @Operation(summary = "更新我的个人资料", description = "只更新请求体中非空的字段（昵称、手机、邮箱）", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/me")
    public ResponseEntity<Void> updateUserProfile(
            @RequestBody UserDtos.ProfileUpdateDto updateDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        userService.updateUserProfile(userDetails.getUsername(), updateDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "更新我的头像", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/avatar")
    public ResponseEntity<Void> updateUserAvatar(
            @RequestBody UserDtos.AvatarUpdateDto avatarDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.updateUserAvatar(userDetails.getUsername(), avatarDto.getAvatarUrl());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "上传头像图片", description = "上传成功后返回文件的公网访问URL", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/avatar/upload")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 使用FileStorageService存储文件并获取URL
        String fileUrl = fileStorageService.storeFile(file);

        // 将原始文件名和可访问的URL返回给前端
        return ResponseEntity.ok(Map.of(
                "fileName", Objects.requireNonNull(file.getOriginalFilename()),
                "fileUrl", fileUrl
        ));
    }

    @Operation(summary = "修改我的密码", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            @RequestBody UserDtos.PasswordUpdateDto passwordDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        userService.updatePassword(userDetails.getUsername(), passwordDto);
        return ResponseEntity.ok("密码修改成功，部分场景下可能需要重新登录。");
    }
}