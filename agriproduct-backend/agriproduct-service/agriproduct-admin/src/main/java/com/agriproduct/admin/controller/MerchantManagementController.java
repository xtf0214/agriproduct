package com.agriproduct.admin.controller;

import com.agriproduct.admin.dto.MerchantAuditRequest;
import com.agriproduct.admin.service.MerchantManagementService;
import com.agriproduct.common.core.domain.Result;
import com.agriproduct.merchant.entity.Merchant;
import com.agriproduct.merchant.vo.MerchantVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商家管理控制器
 */
@RestController
@RequestMapping("/admin/merchant")
@RequiredArgsConstructor
public class MerchantManagementController {

    private final MerchantManagementService merchantManagementService;

    /**
     * 获取商家列表
     */
    @GetMapping("/list")
    public Result<IPage<MerchantVO>> getMerchantList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        IPage<MerchantVO> result = merchantManagementService.getMerchantList(page);
        return Result.success(result);
    }

    /**
     * 审核商家入驻
     */
    @PutMapping("/audit/{id}")
    public Result<Boolean> auditMerchant(@PathVariable Long id,
                                         @RequestBody MerchantAuditRequest request) {
        Boolean result = merchantManagementService.auditMerchant(id, request);
        return Result.success(result);
    }
}
