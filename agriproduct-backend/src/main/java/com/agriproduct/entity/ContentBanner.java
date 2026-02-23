package com.agriproduct.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图实体
 */
@Data
@TableName("content_banner")
public class ContentBanner {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String image;

    /**
     * 链接类型: 1-无, 2-商品, 3-分类
     */
    private Integer linkType;

    private Long linkId;

    private Integer sortOrder;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
