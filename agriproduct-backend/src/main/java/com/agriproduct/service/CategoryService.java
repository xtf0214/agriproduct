package com.agriproduct.service;

import com.agriproduct.entity.ProdCategory;
import com.agriproduct.vo.CategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<ProdCategory> {

    List<CategoryVO> getCategoryTree();

    List<CategoryVO> getTopCategories();

    List<CategoryVO> getChildrenByParentId(Long parentId);

    /**
     * 获取某个分类及其所有子分类的ID列表
     * @param categoryId 分类ID
     * @return 包含该分类及其所有子分类的ID列表
     */
    List<Long> getCategoryAndChildrenIds(Long categoryId);

    /**
     * 判断分类是否有子分类
     * @param categoryId 分类ID
     * @return true-有子分类, false-没有子分类
     */
    boolean hasChildren(Long categoryId);
}
