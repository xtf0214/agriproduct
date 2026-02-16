package com.agriproduct.common.core.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页数量
     */
    private Long pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long totalPages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    public PageResult() {
    }

    public PageResult(IPage<T> page) {
        this.pageNum = page.getCurrent() + 1;
        this.pageSize = page.getSize();
        this.total = page.getTotal();
        this.totalPages = page.getPages();
        this.records = page.getRecords();
        this.hasPrevious = page.getCurrent() > 0;
        this.hasNext = page.getCurrent() + 1 < page.getPages();
    }

    public PageResult(Long pageNum, Long pageSize, Long total, List<T> records) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (total + pageSize - 1) / pageSize;
        this.records = records;
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < totalPages;
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page);
    }

    /**
     * 构建空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(1L, 10L, 0L, List.of());
    }
}
