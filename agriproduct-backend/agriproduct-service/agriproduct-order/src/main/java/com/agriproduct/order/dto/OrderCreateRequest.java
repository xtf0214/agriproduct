package com.agriproduct.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 创建订单请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    /**
     * 购物车项ID列表
     */
    private List<Long> cartItemIds;

    /**
     * 商品ID和数量列表（直接购买）
     */
    private List<OrderItemRequest> items;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单商品项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        /**
         * 商品ID
         */
        private Long productId;

        /**
         * 数量
         */
        private Integer quantity;
    }
}
