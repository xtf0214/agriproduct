package com.agriproduct.product.dto;

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

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 价格排序: asc-升序, desc-降序
     */
    private String priceSort;

    /**
     * 销量排序: asc-升序, desc-降序
     */
    private String salesSort;
}
