package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartVO {

    private Long id;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal productPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private Boolean selected;
}
