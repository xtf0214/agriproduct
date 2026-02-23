package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.BannerRequest;
import com.agriproduct.dto.MerchantAuditRequest;
import com.agriproduct.dto.ProductAuditRequest;
import com.agriproduct.dto.UserStatusRequest;
import com.agriproduct.entity.ContentBanner;
import com.agriproduct.entity.Merchant;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.entity.SysUser;
import com.agriproduct.service.AdminService;
import com.agriproduct.vo.BannerVO;
import com.agriproduct.vo.MerchantVO;
import com.agriproduct.vo.ProductVO;
import com.agriproduct.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台控制器
 */
@Tag(name = "管理后台")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ========== 用户管理 ==========

    @Operation(summary = "获取用户列表")
    @GetMapping("/user/list")
    public Result<IPage<UserInfoVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        IPage<UserInfoVO> result = adminService.getUserList(page);
        return Result.success(result);
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/user/{id}/status")
    public Result<Boolean> updateUserStatus(@PathVariable Long id,
                                             @RequestBody UserStatusRequest request) {
        Boolean result = adminService.updateUserStatus(id, request);
        return Result.success(result);
    }

    // ========== 商家管理 ==========

    @Operation(summary = "获取商家列表")
    @GetMapping("/merchant/list")
    public Result<IPage<MerchantVO>> getMerchantList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        IPage<MerchantVO> result = adminService.getMerchantList(page);
        return Result.success(result);
    }

    @Operation(summary = "审核商家入驻")
    @PutMapping("/merchant/{id}/audit")
    public Result<Boolean> auditMerchant(@PathVariable Long id,
                                          @RequestBody MerchantAuditRequest request) {
        Boolean result = adminService.auditMerchant(id, request);
        return Result.success(result);
    }

    // ========== 商品审核 ==========

    @Operation(summary = "获取待审核商品列表")
    @GetMapping("/product/audit/list")
    public Result<IPage<ProductVO>> getPendingProducts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ProdProduct> page = new Page<>(pageNum, pageSize);
        IPage<ProductVO> result = adminService.getPendingProducts(page);
        return Result.success(result);
    }

    @Operation(summary = "审核商品")
    @PutMapping("/product/{id}/audit")
    public Result<Boolean> auditProduct(@PathVariable Long id,
                                         @RequestBody ProductAuditRequest request) {
        Boolean result = adminService.auditProduct(id, request);
        return Result.success(result);
    }

    // ========== 轮播图管理 ==========

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/banner/list")
    public Result<IPage<BannerVO>> getBannerList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ContentBanner> page = new Page<>(pageNum, pageSize);
        IPage<BannerVO> result = adminService.getBannerList(page);
        return Result.success(result);
    }

    @Operation(summary = "新增轮播图")
    @PostMapping("/banner")
    public Result<Long> createBanner(@RequestBody BannerRequest request) {
        Long bannerId = adminService.createBanner(request);
        return Result.success(bannerId);
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/banner/{id}")
    public Result<Boolean> updateBanner(@PathVariable Long id,
                                        @RequestBody BannerRequest request) {
        Boolean result = adminService.updateBanner(id, request);
        return Result.success(result);
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/banner/{id}")
    public Result<Boolean> deleteBanner(@PathVariable Long id) {
        Boolean result = adminService.deleteBanner(id);
        return Result.success(result);
    }
}
