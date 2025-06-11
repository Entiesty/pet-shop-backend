package com.example.petshopbackend.controller.admin;

import com.example.petshopbackend.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "文件上传（管理员）", description = "用于上传文件到服务器，仅限管理员角色使用")
public class AdminFileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传单个文件，返回包含文件名和访问URL的响应")
    public ResponseEntity<Map<String, String>> uploadFile(
            @Parameter(description = "要上传的文件", required = true)
            @RequestParam("file") MultipartFile file) {

        // 1. 调用Service存储文件，并获取可访问的相对路径
        String relativePath = fileStorageService.storeFile(file);

        // 2. 构建完整的文件访问URL
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(relativePath)
                .toUriString();

        // 3. 返回包含文件名和URL的响应
        return ResponseEntity.ok(Map.of(
                "fileName", Objects.requireNonNull(file.getOriginalFilename()),
                "fileUrl", fileUrl
        ));
    }
}
