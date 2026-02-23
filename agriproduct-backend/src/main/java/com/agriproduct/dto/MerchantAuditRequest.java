package com.agriproduct.dto;

import lombok.Data;

/**
 * 商家审核请求
 */
@Data
public class MerchantAuditRequest {

    /**
     * 审核状态：1-通过，2-拒绝
     */
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;
}
