package com.agriproduct.admin.controller;

import com.agriproduct.admin.service.BannerManagementService;
import com.agriproduct.common.core.domain.Result;
import com.agriproduct.product.entity.ContentBanner;
import com.agriproduct.product.vo.BannerVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理控制器
 */
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class BannerManagementController {

    private final BannerManagementService bannerManagementService;

    /**
     * 获取轮播图列表
     */
    @GetMapping("/list")
    public Result<IPage<BannerVO>> getBannerList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ContentBanner> page = new Page<>(pageNum, pageSize);
        IPage<BannerVO> result = bannerManagementService.getBannerList(page);
        return Result.success(result);
    }

    /**
     * 获取所有轮播图
     */
    @GetMapping("/all")
    public Result<List<BannerVO>> getAllBanners() {
        List<BannerVO> result = bannerManagementService.getAllBanners();
        return Result.success(result);
    }

    /**
     * 添加轮播图
     */
    @PostMapping
    public Result<Boolean> addBanner(@RequestBody ContentBanner banner) {
        Boolean result = bannerManagementService.addBanner(banner);
        return Result.success(result);
    }

    /**
     * 更新轮播图
     */
    @PutMapping
    public Result<Boolean> updateBanner(@RequestBody ContentBanner banner) {
        Boolean result = bannerManagementService.updateBanner(banner);
        return Result.success(result);
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteBanner(@PathVariable Long id) {
        Boolean result = bannerManagementService.deleteBanner(id);
        return Result.success(result);
    }
}
