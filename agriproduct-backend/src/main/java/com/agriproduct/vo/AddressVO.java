package com.agriproduct.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收货地址VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收货地址")
public class AddressVO {

    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "收货人")
    private String receiverName;

    @Schema(description = "收货电话")
    private String receiverPhone;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "完整地址")
    private String fullAddress;

    @Schema(description = "是否默认:0-否,1-是")
    private Integer isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
