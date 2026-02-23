package com.agriproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车添加请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequest {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer quantity;
}
