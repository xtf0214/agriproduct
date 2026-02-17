package com.agriproduct.merchant.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商家信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantVO {
    /**
     * 商家ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺描述
     */
    private String shopDesc;

    /**
     * 店铺Logo
     */
    private String shopLogo;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 审核状态:0-待审核,1-已通过,2-已拒绝
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 获取状态描述
     */
    public static String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "已通过";
            case 2:
                return "已拒绝";
            default:
                return "未知";
        }
    }
}
