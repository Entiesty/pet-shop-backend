package com.example.petshopbackend.controller.admin;

import com.example.petshopbackend.service.FileStorageService;
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
public class AdminFileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
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

