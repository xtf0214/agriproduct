package com.agriproduct.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 统一登录请求
 */
@Data
@Schema(description = "统一登录请求")
public class LoginRequest {

    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "登录类型：user-普通用户，merchant-商家，admin-管理员（可选，不传则自动判断）")
    private String loginType;
}
