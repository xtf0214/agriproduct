package com.agriproduct.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssProperties {

    /**
     * OSS 访问域名，如：oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    /**
     * AccessKeyId
     */
    private String accessKeyId;

    /**
     * AccessKeySecret
     */
    private String accessKeySecret;

    /**
     * Bucket 名称
     */
    private String bucketName;

    /**
     * 上传目录前缀
     */
    private String dirPrefix = "agriproduct";

    /**
     * 自定义访问域名（可选）
     */
    private String publicDomain;
}
