package com.agriproduct.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家商品新增/更新请求
 */
@Data
public class MerchantProductRequest {

    private Long categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    /**
     * 图片JSON字符串
     */
    private String images;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private String detail;
}
