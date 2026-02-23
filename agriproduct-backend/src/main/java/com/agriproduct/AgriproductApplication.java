package com.agriproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 农产品电商平台 - 单体应用启动类
 * 
 * 整合了以下服务：
 * - 用户服务 (User)
 * - 商品服务 (Product)
 * - 订单服务 (Order)
 * - 商家服务 (Merchant)
 * - 管理后台 (Admin)
 * 
 * 运行此应用将启动所有服务，端口8080
 */
@SpringBootApplication(scanBasePackages = "com.agriproduct")
public class AgriproductApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriproductApplication.class, args);
    }
}
