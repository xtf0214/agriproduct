package com.agriproduct.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体
 */
@Data
@TableName("cart_shopping")
public class CartShopping {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private Integer quantity;

    /**
     * 商品名称（非数据库字段，运行时从商品表获取）
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 商品图片（非数据库字段，运行时从商品表获取）
     */
    @TableField(exist = false)
    private String productImage;

    /**
     * 商品价格（非数据库字段，运行时从商品表获取）
     */
    @TableField(exist = false)
    private BigDecimal productPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
