package com.agriproduct.merchant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商家服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.agriproduct")
@MapperScan("com.agriproduct.merchant.mapper")
public class MerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }
}
