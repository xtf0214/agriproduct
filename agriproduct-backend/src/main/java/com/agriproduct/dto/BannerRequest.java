package com.agriproduct.dto;

import lombok.Data;

/**
 * 轮播图新增/更新请求
 */
@Data
public class BannerRequest {

    private String title;

    private String image;

    /**
     * 链接类型: 1-无, 2-商品, 3-分类
     */
    private Integer linkType;

    private Long linkId;

    private Integer sortOrder;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;
}
