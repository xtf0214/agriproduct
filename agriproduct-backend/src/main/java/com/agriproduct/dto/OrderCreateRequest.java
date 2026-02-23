package com.agriproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 购物车商品ID列表（从购物车下单）
     */
    private List<Long> cartIds;

    /**
     * 商品ID（直接购买）
     */
    private Long productId;

    /**
     * 数量（直接购买）
     */
    private Integer quantity;

    /**
     * 订单备注
     */
    private String remark;
}
