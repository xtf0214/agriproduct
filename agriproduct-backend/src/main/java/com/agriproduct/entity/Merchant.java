package com.agriproduct.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家实体
 */
@Data
@TableName("mer_merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String shopName;

    private String shopDesc;

    private String businessLicense;

    private String shopLogo;

    private String contactPhone;

    private String contactName;

    /**
     * 审核状态:0-待审核,1-已通过,2-已拒绝
     */
    private Integer status;

    private String auditRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
