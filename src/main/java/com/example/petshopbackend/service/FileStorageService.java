package com.example.petshopbackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * 存储上传的文件
     * @param file 上传的文件对象
     * @return 返回文件可访问的URL路径
     */
    String storeFile(MultipartFile file);
}
