package com.agriproduct.merchant.dto;

import lombok.Data;

/**
 * 商家入驻申请请求
 */
@Data
public class MerchantApplyRequest {
    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺描述
     */
    private String shopDesc;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系人
     */
    private String contactName;
}
