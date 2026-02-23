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
}
