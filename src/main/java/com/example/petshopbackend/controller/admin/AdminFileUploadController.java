package com.example.petshopbackend.controller.admin;

import com.example.petshopbackend.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@Tag(name = "后台-文件管理模块", description = "提供文件上传等功能")
@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFileUploadController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "上传文件（图片/视频）", description = "上传成功后返回文件的公网访问URL")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        // [MODIFIED] 直接使用 FileStorageService 返回的完整、正确的MinIO URL
        String fileUrl = fileStorageService.storeFile(file);

        // 您可以将返回的fileUrl保存到数据库的相应字段中
        // 例如，在调用更新商品接口时，将这个URL作为请求体的一部分

        // 将原始文件名和可访问的URL返回给前端
        return ResponseEntity.ok(Map.of(
                "fileName", Objects.requireNonNull(file.getOriginalFilename()),
                "fileUrl", fileUrl
        ));
    }
}