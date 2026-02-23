package com.agriproduct.dto;

import lombok.Data;

/**
 * 商家入驻申请请求
 */
@Data
public class MerchantApplyRequest {

    private String shopName;

    private String shopDesc;

    private String businessLicense;

    private String contactPhone;

    private String contactName;
}
