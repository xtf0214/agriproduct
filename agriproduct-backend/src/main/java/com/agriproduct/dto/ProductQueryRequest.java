package com.agriproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductQueryRequest {

    private Long categoryId;

    private String keyword;

    private String priceSort;

    private String salesSort;
}
