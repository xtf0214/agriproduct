package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分类视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {

    private Long id;

    private String name;

    private Long parentId;

    private String icon;

    private Integer sortOrder;

    private List<CategoryVO> children;

    private Boolean hasChildren;
}
