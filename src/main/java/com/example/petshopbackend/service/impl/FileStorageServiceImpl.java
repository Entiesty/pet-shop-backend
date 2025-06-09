package com.example.petshopbackend.service.impl;

import com.example.petshopbackend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    // 从 application.yml 中注入文件上传目录
    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // 如果目录不存在，则创建
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建用于存储上传文件的目录！", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // 规范化文件名
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // 检查文件名是否包含无效字符
            if (originalFileName.contains("..")) {
                throw new RuntimeException("文件名包含无效的路径序列 " + originalFileName);
            }

            // 为了避免重名，我们使用UUID生成一个唯一的文件名
            String fileExtension = "";
            try {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } catch (Exception e) {
                // 忽略没有扩展名的文件
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;


            // 构建最终的文件存储路径
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            // 将上传的文件流复制到目标位置
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回可供Web访问的相对路径
            return "/" + uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("无法存储文件 " + originalFileName + "。请再试一次！", ex);
        }
    }
}
