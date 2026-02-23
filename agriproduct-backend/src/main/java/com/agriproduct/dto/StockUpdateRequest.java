package com.agriproduct.dto;

import lombok.Data;

/**
 * 商品库存更新请求
 */
@Data
public class StockUpdateRequest {

    private Integer stock;
}
