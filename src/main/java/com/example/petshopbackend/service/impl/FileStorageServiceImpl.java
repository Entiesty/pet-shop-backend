package com.example.petshopbackend.service.impl;

import com.example.petshopbackend.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;


    @Override
    public String storeFile(MultipartFile file) {
        try {
            // 1. 检查存储桶是否存在，如果不存在则自动创建
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 2. 生成一个唯一的文件名以避免冲突
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

            // 3. 使用putObject方法将文件流上传到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // 指定存储桶
                            .object(uniqueFileName) // 指定文件名
                            .stream(file.getInputStream(), file.getSize(), -1) // 文件流和大小
                            .contentType(file.getContentType()) // 文件类型
                            .build());

            // 4. 返回文件的完整可访问URL
            // 格式为: http://<endpoint>/<bucket-name>/<object-name>
            return endpoint + "/" + bucketName + "/" + uniqueFileName;

        } catch (Exception e) {
            // 在真实项目中，应该有更详细的异常分类处理
            throw new RuntimeException("文件上传到MinIO时发生错误！", e);
        }
    }
}