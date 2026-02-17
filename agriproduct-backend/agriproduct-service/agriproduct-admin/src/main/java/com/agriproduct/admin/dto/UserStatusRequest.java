package com.agriproduct.admin.dto;

import lombok.Data;

/**
 * 用户状态更新请求
 */
@Data
public class UserStatusRequest {
    /**
     * 状态:0-禁用,1-正常
     */
    private Integer status;
}
