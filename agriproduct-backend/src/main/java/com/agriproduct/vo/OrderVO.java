package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long merchantId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    /**
     * 状态: 1-待付款, 2-待发货, 3-待收货, 4-已完成, 5-已取消
     */
    private Integer status;

    private String statusDesc;

    private Integer payStatus;

    private LocalDateTime payTime;

    private LocalDateTime shipTime;

    private LocalDateTime finishTime;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String remark;

    private LocalDateTime createTime;

    private List<OrderItemVO> items;

    /**
     * 获取状态描述
     */
    public static String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "待付款";
            case 2:
                return "待发货";
            case 3:
                return "待收货";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知";
        }
    }
}
