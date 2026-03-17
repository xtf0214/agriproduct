package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.entity.ProdCategory;
import com.agriproduct.mapper.ProdCategoryMapper;
import com.agriproduct.service.CategoryService;
import com.agriproduct.vo.CategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<ProdCategoryMapper, ProdCategory> implements CategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<ProdCategory> allCategories = list(
                new LambdaQueryWrapper<ProdCategory>()
                        .orderByAsc(ProdCategory::getSortOrder)
        );

        List<ProdCategory> topCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

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

    @Override
    public List<Long> getCategoryAndChildrenIds(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        
        // 递归获取所有子分类ID
        collectChildrenIds(categoryId, ids);
        
        return ids;
    }

    /**
     * 递归收集所有子分类ID
     */
    private void collectChildrenIds(Long parentId, List<Long> ids) {
        List<ProdCategory> children = list(new LambdaQueryWrapper<ProdCategory>()
                .eq(ProdCategory::getParentId, parentId)
                .select(ProdCategory::getId));
        
        for (ProdCategory child : children) {
            ids.add(child.getId());
            collectChildrenIds(child.getId(), ids);
        }
    }

    @Override
    public boolean hasChildren(Long categoryId) {
        long count = count(new LambdaQueryWrapper<ProdCategory>()
                .eq(ProdCategory::getParentId, categoryId));
        return count > 0;
    }

    private CategoryVO buildCategoryTree(ProdCategory category, List<ProdCategory> allCategories) {
        CategoryVO vo = convertToVO(category);

        List<CategoryVO> children = allCategories.stream()
                .filter(c -> category.getId().equals(c.getParentId()))
                .map(child -> buildCategoryTree(child, allCategories))
                .collect(Collectors.toList());

        vo.setChildren(children);
        vo.setHasChildren(!children.isEmpty());

        return vo;
    }

    private CategoryVO convertToVO(ProdCategory category) {
        CategoryVO vo = BeanUtil.copyProperties(category, CategoryVO.class);
        vo.setHasChildren(false);
        return vo;
    }
}
