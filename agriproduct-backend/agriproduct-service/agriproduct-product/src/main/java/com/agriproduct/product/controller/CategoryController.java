package com.agriproduct.product.controller;

import com.agriproduct.product.service.CategoryService;
import com.agriproduct.product.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Tag(name = "分类管理")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类树
     */
    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public List<CategoryVO> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    /**
     * 获取一级分类
     */
    @Operation(summary = "获取一级分类")
    @GetMapping("/top")
    public List<CategoryVO> getTopCategories() {
        return categoryService.getTopCategories();
    }

    /**
     * 获取子分类
     */
    @Operation(summary = "获取子分类")
    @GetMapping("/{parentId}/children")
    public List<CategoryVO> getChildren(@PathVariable Long parentId) {
        return categoryService.getChildrenByParentId(parentId);
    }
}
