package com.agriproduct.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品销量排行
 */
@Data
public class ProductSalesVO {

    private Long productId;

    private String productName;

    private String productImage;

    private Integer salesCount;

    private BigDecimal salesAmount;
}
