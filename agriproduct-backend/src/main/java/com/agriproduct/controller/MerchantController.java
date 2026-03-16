package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.MerchantApplyRequest;
import com.agriproduct.dto.MerchantProductRequest;
import com.agriproduct.dto.StockUpdateRequest;
import com.agriproduct.entity.OrderOrder;
import com.agriproduct.service.FileStorageService;
import com.agriproduct.service.MerchantService;
import com.agriproduct.service.OrderService;
import com.agriproduct.service.ProductService;
import com.agriproduct.entity.Merchant;
import com.agriproduct.vo.MerchantVO;
import com.agriproduct.vo.OrderVO;
import com.agriproduct.vo.ProductSalesVO;
import com.agriproduct.vo.ProductVO;
import com.agriproduct.vo.SalesStatisticsVO;
import com.agriproduct.vo.StatisticsOverviewVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 商家控制器
 */
@Tag(name = "商家管理")
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final OrderService orderService;
    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @Operation(summary = "上传图片到 OSS")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestHeader("X-User-Id") Long userId,
                                              @RequestParam("file") MultipartFile file) {
        merchantService.getMerchantByUserId(userId);
        String url = fileStorageService.upload(file, "merchant/product");
        return Result.success(Map.of("url", url));
    }

    @Operation(summary = "商家入驻申请")
    @PostMapping("/apply")
    public Result<Long> applyForMerchant(@RequestHeader("X-User-Id") Long userId,
                                          @RequestBody MerchantApplyRequest request) {
        Long merchantId = merchantService.applyForMerchant(userId, request);
        return Result.success(merchantId);
    }

    @Operation(summary = "获取商家信息")
    @GetMapping("/{id}")
    public Result<MerchantVO> getMerchantInfo(@PathVariable Long id) {
        MerchantVO merchantVO = merchantService.getMerchantInfo(id);
        return Result.success(merchantVO);
    }

    @Operation(summary = "获取当前用户的商家信息")
    @GetMapping("/info")
    public Result<MerchantVO> getCurrentMerchantInfo(@RequestHeader("X-User-Id") Long userId) {
        MerchantVO merchantVO = merchantService.getMerchantByUserId(userId);
        return Result.success(merchantVO);
    }

    @Operation(summary = "更新商家信息")
    @PutMapping("/{id}")
    public Result<Boolean> updateMerchant(@PathVariable Long id,
                                           @RequestHeader("X-User-Id") Long userId,
                                           @RequestBody MerchantApplyRequest request) {
        Boolean result = merchantService.updateMerchant(id, userId, request);
        return Result.success(result);
    }

    @Operation(summary = "获取商家订单列表")
    @GetMapping("/orders")
    public Result<IPage<OrderVO>> getMerchantOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 获取商家信息
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Page<OrderOrder> page = new Page<>(pageNum, pageSize);
        IPage<OrderVO> result = orderService.getMerchantOrders(page, merchant.getId());
        return Result.success(result);
    }

    @Operation(summary = "订单发货")
    @PutMapping("/order/{orderId}/ship")
    public Result<Void> shipOrder(@PathVariable Long orderId,
                                   @RequestHeader("X-User-Id") Long userId) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        orderService.shipOrder(orderId, merchant.getId());
        return Result.success();
    }

    @Operation(summary = "获取商家商品列表")
    @GetMapping("/product/list")
    public Result<IPage<ProductVO>> getMerchantProductList(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Page<com.agriproduct.entity.ProdProduct> page = new Page<>(pageNum, pageSize);
        IPage<ProductVO> result = productService.getMerchantProductList(page, merchant.getId(), keyword, status);
        return Result.success(result);
    }

    @Operation(summary = "新增商家商品")
    @PostMapping("/product")
    public Result<Long> createMerchantProduct(@RequestHeader("X-User-Id") Long userId,
                                              @RequestBody MerchantProductRequest request) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Long productId = productService.createMerchantProduct(merchant.getId(), request);
        return Result.success(productId);
    }

    @Operation(summary = "更新商家商品")
    @PutMapping("/product/{id}")
    public Result<Boolean> updateMerchantProduct(@PathVariable Long id,
                                                 @RequestHeader("X-User-Id") Long userId,
                                                 @RequestBody MerchantProductRequest request) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Boolean result = productService.updateMerchantProduct(id, merchant.getId(), request);
        return Result.success(result);
    }

    @Operation(summary = "删除商家商品")
    @DeleteMapping("/product/{id}")
    public Result<Boolean> deleteMerchantProduct(@PathVariable Long id,
                                                 @RequestHeader("X-User-Id") Long userId) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Boolean result = productService.deleteMerchantProduct(id, merchant.getId());
        return Result.success(result);
    }

    @Operation(summary = "更新商品库存")
    @PutMapping("/product/{id}/stock")
    public Result<Boolean> updateMerchantProductStock(@PathVariable Long id,
                                                      @RequestHeader("X-User-Id") Long userId,
                                                      @RequestBody StockUpdateRequest request) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Boolean result = productService.updateMerchantProductStock(id, merchant.getId(), request.getStock());
        return Result.success(result);
    }

    @Operation(summary = "商品上架")
    @PutMapping("/product/{id}/publish")
    public Result<Boolean> publishMerchantProduct(@PathVariable Long id,
                                                  @RequestHeader("X-User-Id") Long userId) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Boolean result = productService.publishMerchantProduct(id, merchant.getId());
        return Result.success(result);
    }

    @Operation(summary = "商品下架")
    @PutMapping("/product/{id}/unpublish")
    public Result<Boolean> unpublishMerchantProduct(@PathVariable Long id,
                                                    @RequestHeader("X-User-Id") Long userId) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        Boolean result = productService.unpublishMerchantProduct(id, merchant.getId());
        return Result.success(result);
    }

    @Operation(summary = "商家统计概览")
    @GetMapping("/statistics/overview")
    public Result<StatisticsOverviewVO> getStatisticsOverview(@RequestHeader("X-User-Id") Long userId) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        StatisticsOverviewVO overview = merchantService.getStatisticsOverview(merchant.getId());
        return Result.success(overview);
    }

    @Operation(summary = "按日期销售统计")
    @GetMapping("/statistics/sales")
    public Result<List<SalesStatisticsVO>> getSalesStatistics(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        List<SalesStatisticsVO> stats = merchantService.getSalesStatistics(merchant.getId(), startDate, endDate);
        return Result.success(stats);
    }

    @Operation(summary = "商品销量排行")
    @GetMapping("/statistics/products")
    public Result<List<ProductSalesVO>> getProductSalesRank(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        MerchantVO merchant = merchantService.getMerchantByUserId(userId);
        List<ProductSalesVO> result = merchantService.getProductSalesRank(merchant.getId(), limit);
        return Result.success(result);
    }
}
