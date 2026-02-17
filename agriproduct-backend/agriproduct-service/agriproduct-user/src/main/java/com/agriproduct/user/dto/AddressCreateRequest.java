package com.agriproduct.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 收货地址创建/修改请求
 */
@Data
@Schema(description = "收货地址请求")
public class AddressCreateRequest {

    @Schema(description = "收货人", required = true)
    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    @Schema(description = "收货电话", required = true)
    @NotBlank(message = "收货电话不能为空")
    private String receiverPhone;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "详细地址", required = true)
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    @Schema(description = "是否默认:0-否,1-是")
    private Integer isDefault;
}
