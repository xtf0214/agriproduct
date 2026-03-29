package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.BannerRequest;
import com.agriproduct.dto.CategoryRequest;
import com.agriproduct.dto.MerchantAuditRequest;
import com.agriproduct.dto.ProductAuditRequest;
import com.agriproduct.dto.UserStatusRequest;
import com.agriproduct.entity.ContentBanner;
import com.agriproduct.entity.Merchant;
import com.agriproduct.entity.OrderOrder;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.entity.SysUser;
import com.agriproduct.service.AdminService;
import com.agriproduct.service.FileStorageService;
import com.agriproduct.vo.AdminStatisticsOverviewVO;
import com.agriproduct.vo.BannerVO;
import com.agriproduct.vo.CategorySalesVO;
import com.agriproduct.vo.CategoryVO;
import com.agriproduct.vo.DailyOrderStatisticsVO;
import com.agriproduct.vo.MerchantVO;
import com.agriproduct.vo.OrderVO;
import com.agriproduct.vo.ProductVO;
import com.agriproduct.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 管理后台控制器
 */
@Tag(name = "管理后台")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final FileStorageService fileStorageService;

    // ========== 用户管理 ==========

    @Operation(summary = "获取用户列表")
    @GetMapping("/user/list")
    public Result<IPage<UserInfoVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        IPage<UserInfoVO> result = adminService.getUserList(page, username, phone);
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
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) Integer status) {
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        IPage<MerchantVO> result = adminService.getMerchantList(page, shopName, status);
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

    // ========== 分类管理 ==========

    @Operation(summary = "获取分类树形结构")
    @GetMapping("/category/tree")
    public Result<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> tree = adminService.getCategoryTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取一级分类列表")
    @GetMapping("/category/top")
    public Result<List<CategoryVO>> getTopCategories() {
        List<CategoryVO> categories = adminService.getTopCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取子分类列表")
    @GetMapping("/category/children/{parentId}")
    public Result<List<CategoryVO>> getChildrenCategories(@PathVariable Long parentId) {
        List<CategoryVO> categories = adminService.getChildrenCategories(parentId);
        return Result.success(categories);
    }

    @Operation(summary = "新增分类")
    @PostMapping("/category")
    public Result<Long> createCategory(@Valid @RequestBody CategoryRequest request) {
        Long categoryId = adminService.createCategory(request);
        return Result.success(categoryId);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/category/{id}")
    public Result<Boolean> updateCategory(@PathVariable Long id,
                                          @Valid @RequestBody CategoryRequest request) {
        Boolean result = adminService.updateCategory(id, request);
        return Result.success(result);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/category/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        Boolean result = adminService.deleteCategory(id);
        return Result.success(result);
    }

    @Operation(summary = "上传分类图标")
    @PostMapping("/category/upload")
    public Result<Map<String, String>> uploadCategoryIcon(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.upload(file, "category/icon");
        return Result.success(Map.of("url", url));
    }

    // ========== 订单管理 ==========

    @Operation(summary = "获取订单列表")
    @GetMapping("/order/list")
    public Result<IPage<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long merchantId) {
        Page<OrderOrder> page = new Page<>(pageNum, pageSize);
        IPage<OrderVO> result = adminService.getOrderList(page, orderNo, status, userId, merchantId);
        return Result.success(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/order/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        OrderVO order = adminService.getOrderDetail(id);
        return Result.success(order);
    }

    @Operation(summary = "发货")
    @PutMapping("/order/{id}/ship")
    public Result<Boolean> shipOrder(@PathVariable Long id) {
        Boolean result = adminService.shipOrder(id);
        return Result.success(result);
    }

    @Operation(summary = "取消订单")
    @PutMapping("/order/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Long id) {
        Boolean result = adminService.cancelOrder(id);
        return Result.success(result);
    }

    // ========== 统计数据 ==========

    @Operation(summary = "获取统计概览")
    @GetMapping("/statistics/overview")
    public Result<AdminStatisticsOverviewVO> getStatisticsOverview() {
        AdminStatisticsOverviewVO overview = adminService.getStatisticsOverview();
        return Result.success(overview);
    }

    @Operation(summary = "获取每日订单统计")
    @GetMapping("/statistics/orders/daily")
    public Result<List<DailyOrderStatisticsVO>> getDailyOrderStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<DailyOrderStatisticsVO> statistics = adminService.getDailyOrderStatistics(startDate, endDate);
        return Result.success(statistics);
    }

    @Operation(summary = "获取分类销售统计")
    @GetMapping("/statistics/category")
    public Result<List<CategorySalesVO>> getCategorySales() {
        List<CategorySalesVO> statistics = adminService.getCategorySales();
        return Result.success(statistics);
    }
}
