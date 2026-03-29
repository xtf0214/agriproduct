package com.agriproduct.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类请求DTO
 */
@Data
@Schema(description = "分类请求")
public class CategoryRequest {

    @Schema(description = "分类ID（更新时需要）")
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", required = true)
    private String name;

    @Schema(description = "父分类ID（为空或0表示一级分类）")
    private Long parentId;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序号（数字越小越靠前）")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
