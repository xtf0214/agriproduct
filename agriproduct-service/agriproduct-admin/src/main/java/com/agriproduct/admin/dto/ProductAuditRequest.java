package com.agriproduct.admin.dto;

import lombok.Data;

/**
 * 商品审核请求
 */
@Data
public class ProductAuditRequest {
    /**
     * 审核状态:1-通过,2-拒绝
     */
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;
}
