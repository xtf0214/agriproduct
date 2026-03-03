package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 每日订单统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyOrderStatisticsVO {

    private String date;

    private Long orderCount;

    private BigDecimal totalAmount;
}
