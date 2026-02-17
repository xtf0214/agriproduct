package com.agriproduct.product.controller;

import com.agriproduct.product.service.BannerService;
import com.agriproduct.product.service.CategoryService;
import com.agriproduct.product.service.ProductService;
import com.agriproduct.product.vo.BannerVO;
import com.agriproduct.product.vo.CategoryVO;
import com.agriproduct.product.vo.ProductVO;
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

    /**
     * 获取轮播图
     */
    @Operation(summary = "获取轮播图")
    @GetMapping("/banner")
    public List<BannerVO> getBanners() {
        return bannerService.getActiveBanners();
    }

    /**
     * 获取分类列表
     */
    @Operation(summary = "获取分类列表")
    @GetMapping("/categories")
    public List<CategoryVO> getCategories() {
        return categoryService.getTopCategories();
    }

    /**
     * 获取推荐商品
     */
    @Operation(summary = "获取推荐商品")
    @GetMapping("/recommend")
    public List<ProductVO> getRecommendProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        return productService.getRecommendProducts(limit);
    }

    /**
     * 首页数据（轮播图+分类+推荐商品）
     */
    @Operation(summary = "首页数据")
    @GetMapping
    public Map<String, Object> getHomeData(
            @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> result = new HashMap<>();
        result.put("banners", bannerService.getActiveBanners());
        result.put("categories", categoryService.getTopCategories());
        result.put("recommendProducts", productService.getRecommendProducts(limit));
        return result;
    }
}
