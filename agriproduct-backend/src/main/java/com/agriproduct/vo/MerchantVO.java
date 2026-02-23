package com.agriproduct.vo;

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

    private Long id;

    private Long userId;

    private String shopName;

    private String shopDesc;

    private String shopLogo;

    private String contactPhone;

    private String contactName;

    /**
     * 审核状态:0-待审核,1-已通过,2-已拒绝
     */
    private Integer status;

    private String statusDesc;

    private String auditRemark;

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
