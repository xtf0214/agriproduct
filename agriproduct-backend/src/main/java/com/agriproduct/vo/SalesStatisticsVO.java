package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 按日期销售统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatisticsVO {

    private String date;

    private BigDecimal amount;

    private Long count;
}
