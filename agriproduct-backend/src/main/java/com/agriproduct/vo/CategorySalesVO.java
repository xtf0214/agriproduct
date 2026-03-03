package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 分类销售统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySalesVO {

    private Long categoryId;

    private String categoryName;

    private Long productCount;

    private BigDecimal salesAmount;
}
