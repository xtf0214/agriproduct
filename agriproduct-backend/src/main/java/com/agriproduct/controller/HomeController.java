package com.agriproduct.controller;

import com.agriproduct.service.CategoryService;
import com.agriproduct.service.ProductService;
import com.agriproduct.service.BannerService;
import com.agriproduct.dto.ProductQueryRequest;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.vo.BannerVO;
import com.agriproduct.vo.CategoryVO;
import com.agriproduct.vo.ProductVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 */
@Tag(name = "首页")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final BannerService bannerService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Operation(summary = "获取轮播图")
    @GetMapping("/banner")
    public List<BannerVO> getBanners() {
        return bannerService.getActiveBanners();
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/category")
    public List<CategoryVO> getCategories() {
        return categoryService.getTopCategories();
    }

    @Operation(summary = "获取分类树")
    @GetMapping("/category/tree")
    public List<CategoryVO> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @Operation(summary = "获取推荐商品")
    @GetMapping("/recommend")
    public List<ProductVO> getRecommendProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return productService.getRecommendProducts(limit);
    }

    @Operation(summary = "获取首页数据")
    @GetMapping
    public Map<String, Object> getHomeData() {
        Map<String, Object> data = new HashMap<>();
        data.put("banners", bannerService.getActiveBanners());
        data.put("categories", categoryService.getTopCategories());
        data.put("recommends", productService.getRecommendProducts(10));
        return data;
    }
}
