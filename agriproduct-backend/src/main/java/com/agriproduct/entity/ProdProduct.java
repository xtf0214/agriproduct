package com.agriproduct.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("prod_product")
public class ProdProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private String images;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private String detail;

    /**
     * 状态: 0-下架, 1-上架, 2-审核中
     */
    private Integer status;

    /**
     * 审核状态: 0-待审核, 1-已通过, 2-已拒绝
     */
    private Integer auditStatus;

    private String auditReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
