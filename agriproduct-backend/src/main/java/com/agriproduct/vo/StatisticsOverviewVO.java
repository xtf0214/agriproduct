package com.agriproduct.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家统计概览
 */
@Data
public class StatisticsOverviewVO {

    private BigDecimal todaySales;

    private Long todayOrders;

    private Long totalProducts;

    private Long pendingOrders;
}
