package com.agriproduct.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("order_order")
public class OrderOrder {

    @TableId(type = IdType.AUTO)
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

    /**
     * 支付状态: 0-未支付, 1-已支付
     */
    private Integer payStatus;

    private LocalDateTime payTime;

    private LocalDateTime shipTime;

    private LocalDateTime finishTime;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
