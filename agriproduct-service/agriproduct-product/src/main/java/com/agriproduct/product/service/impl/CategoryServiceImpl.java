package com.agriproduct.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.product.entity.ProdCategory;
import com.agriproduct.product.mapper.ProdCategoryMapper;
import com.agriproduct.product.service.CategoryService;
import com.agriproduct.product.vo.CategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<ProdCategoryMapper, ProdCategory> implements CategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 获取所有分类
        List<ProdCategory> allCategories = list(
                new LambdaQueryWrapper<ProdCategory>()
                        .orderByAsc(ProdCategory::getSortOrder)
        );

        // 获取一级分类
        List<ProdCategory> topCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        // 构建树形结构
        return topCategories.stream()
                .map(category -> buildCategoryTree(category, allCategories))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getTopCategories() {
        return list(new LambdaQueryWrapper<ProdCategory>()
                        .eq(ProdCategory::getParentId, 0)
                        .or()
                        .isNull(ProdCategory::getParentId)
                        .orderByAsc(ProdCategory::getSortOrder)
                ).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getChildrenByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<ProdCategory>()
                        .eq(ProdCategory::getParentId, parentId)
                        .orderByAsc(ProdCategory::getSortOrder)
                ).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 递归构建分类树
     */
    private CategoryVO buildCategoryTree(ProdCategory category, List<ProdCategory> allCategories) {
        CategoryVO vo = convertToVO(category);

        // 查找子分类
        List<CategoryVO> children = allCategories.stream()
                .filter(c -> category.getId().equals(c.getParentId()))
                .map(child -> buildCategoryTree(child, allCategories))
                .collect(Collectors.toList());

        vo.setChildren(children);
        vo.setHasChildren(!children.isEmpty());

        return vo;
    }

    /**
     * 转换为VO
     */
    private CategoryVO convertToVO(ProdCategory category) {
        return BeanUtil.copyProperties(category, CategoryVO.class);
    }
}
