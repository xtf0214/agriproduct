package com.agriproduct.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.agriproduct.config.AliyunOssProperties;
import com.agriproduct.enums.ResultCode;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 阿里云 OSS 文件存储实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunOssFileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    private final AliyunOssProperties ossProperties;

    @Override
    public String upload(MultipartFile file, String scene) {
        validateConfig();
        validateFile(file);

        String ext = getFileExtension(file.getOriginalFilename());
        String objectKey = buildObjectKey(scene, ext);

        OSS ossClient = null;
        try (InputStream inputStream = file.getInputStream()) {
            ossClient = new OSSClientBuilder().build(
                    ossProperties.getEndpoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret()
            );

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(resolveContentType(ext));

            ossClient.putObject(ossProperties.getBucketName(), objectKey, inputStream, metadata);

            String url = buildPublicUrl(objectKey);
            log.info("文件上传成功: bucket={}, objectKey={}, url={}", ossProperties.getBucketName(), objectKey, url);
            return url;
        } catch (IOException e) {
            log.error("读取上传文件失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR, "读取上传文件失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传 OSS 失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR, "上传 OSS 失败");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private void validateConfig() {
        if (StrUtil.hasBlank(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                ossProperties.getBucketName())) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR, "OSS 配置不完整，请检查 endpoint/accessKey/bucket");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
        }

        String ext = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            throw new BusinessException(ResultCode.FILE_TYPE_ERROR, "仅支持 jpg/jpeg/png/gif/webp/bmp 图片");
        }
    }

    private String buildObjectKey(String scene, String ext) {
        String prefix = StrUtil.blankToDefault(ossProperties.getDirPrefix(), "agriproduct").trim();
        String safeScene = StrUtil.blankToDefault(scene, "common").trim();
        LocalDate now = LocalDate.now();
        return String.format(
                "%s/%s/%d/%02d/%02d/%s.%s",
                prefix,
                safeScene,
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                UUID.randomUUID().toString().replace("-", ""),
                ext
        );
    }

    private String buildPublicUrl(String objectKey) {
        String customDomain = StrUtil.trim(ossProperties.getPublicDomain());
        if (StrUtil.isNotBlank(customDomain)) {
            String domain = customDomain;
            if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                domain = "https://" + domain;
            }
            return domain.replaceAll("/+$", "") + "/" + objectKey;
        }
        return String.format("https://%s.%s/%s", ossProperties.getBucketName(), ossProperties.getEndpoint(), objectKey);
    }

    private String getFileExtension(String filename) {
        if (StrUtil.isBlank(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String resolveContentType(String ext) {
        switch (ext) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "bmp":
                return "image/bmp";
            default:
                return "application/octet-stream";
        }
    }
}
