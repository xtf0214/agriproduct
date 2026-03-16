package com.agriproduct.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务
 */
public interface FileStorageService {

    /**
     * 上传文件并返回可访问 URL
     *
     * @param file 文件
     * @param scene 上传场景（如 merchant/product）
     * @return 公开访问 URL
     */
    String upload(MultipartFile file, String scene);
}
