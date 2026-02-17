package com.agriproduct.product.service;

import com.agriproduct.product.entity.ProdCategory;
import com.agriproduct.product.vo.CategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<ProdCategory> {

    /**
     * 获取分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取一级分类列表
     */
    List<CategoryVO> getTopCategories();

    /**
     * 根据父分类ID获取子分类
     */
    List<CategoryVO> getChildrenByParentId(Long parentId);
}
