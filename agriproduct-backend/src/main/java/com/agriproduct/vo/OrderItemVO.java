package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单商品明细VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {

    private Long id;

    private Long orderId;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalAmount;
}
