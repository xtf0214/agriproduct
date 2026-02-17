package com.agriproduct.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理后台服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.agriproduct")
@MapperScan({"com.agriproduct.admin.mapper", "com.agriproduct.product.mapper", "com.agriproduct.user.mapper", "com.agriproduct.merchant.mapper"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
