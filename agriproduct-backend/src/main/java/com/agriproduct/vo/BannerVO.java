package com.agriproduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 轮播图视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerVO {

    private Long id;

    private String title;

    private String image;

    private Integer linkType;

    private Long linkId;

    private Integer sortOrder;
}
