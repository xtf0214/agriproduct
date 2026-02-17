package com.agriproduct.admin.controller;

import com.agriproduct.admin.dto.ProductAuditRequest;
import com.agriproduct.admin.service.ProductAuditService;
import com.agriproduct.common.core.domain.Result;
import com.agriproduct.product.entity.ProdProduct;
import com.agriproduct.product.vo.ProductVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品审核控制器
 */
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductAuditController {

    private final ProductAuditService productAuditService;

    /**
     * 获取待审核商品列表
     */
    @GetMapping("/audit/list")
    public Result<IPage<ProductVO>> getPendingProducts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ProdProduct> page = new Page<>(pageNum, pageSize);
        IPage<ProductVO> result = productAuditService.getPendingProducts(page);
        return Result.success(result);
    }

    /**
     * 审核商品
     */
    @PutMapping("/audit/{id}")
    public Result<Boolean> auditProduct(@PathVariable Long id,
                                        @RequestBody ProductAuditRequest request) {
        Boolean result = productAuditService.auditProduct(id, request);
        return Result.success(result);
    }
}
