package com.agriproduct.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式：asc/desc
     */
    private String orderDirection = "desc";

    /**
     * 获取MyBatis-Plus的页码（从0开始）
     */
    public long getMybatisPlusPageNum() {
        return pageNum != null && pageNum > 0 ? pageNum - 1 : 0;
    }

    /**
     * 获取每页数量
     */
    public long getPageSizeValue() {
        return pageSize != null && pageSize > 0 ? pageSize : 10;
    }
}
