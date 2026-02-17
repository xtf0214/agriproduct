package com.agriproduct.product.vo;

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

    /**
     * 轮播图ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 链接类型
     */
    private Integer linkType;

    /**
     * 关联ID
     */
    private Long linkId;

    /**
     * 排序
     */
    private Integer sortOrder;
}
