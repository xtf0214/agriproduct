package com.agriproduct.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理员统计概览
 */
@Data
public class AdminStatisticsOverviewVO {

    private Long totalUsers;

    private Long totalMerchants;

    private Long totalProducts;

    private Long totalOrders;
}
