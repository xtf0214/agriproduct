package com.agriproduct.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一登录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一登录响应")
public class LoginResponse {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "用户类型:1-普通用户,2-商家,3-管理员")
    private Integer userType;

    @Schema(description = "用户类型描述")
    private String userTypeDesc;

    // ========== 商家专属字段 ==========

    @Schema(description = "商家ID（仅商家类型返回）")
    private Long merchantId;

    @Schema(description = "店铺名称（仅商家类型返回）")
    private String shopName;

    @Schema(description = "商家审核状态:0-待审核,1-已通过,2-已拒绝（仅商家类型返回）")
    private Integer merchantStatus;

    /**
     * 获取用户类型描述
     */
    public static String getUserTypeDesc(Integer userType) {
        if (userType == null) {
            return "未知";
        }
        switch (userType) {
            case 1:
                return "普通用户";
            case 2:
                return "商家";
            case 3:
                return "管理员";
            default:
                return "未知";
        }
    }
}
