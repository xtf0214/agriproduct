package com.agriproduct.merchant.controller;

import com.agriproduct.common.core.domain.Result;
import com.agriproduct.merchant.dto.MerchantApplyRequest;
import com.agriproduct.merchant.service.MerchantService;
import com.agriproduct.merchant.vo.MerchantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    /**
     * 商家入驻申请
     */
    @PostMapping("/apply")
    public Result<Long> applyForMerchant(@RequestHeader("X-User-Id") Long userId,
                                          @RequestBody MerchantApplyRequest request) {
        Long merchantId = merchantService.applyForMerchant(userId, request);
        return Result.success(merchantId);
    }

    /**
     * 获取商家信息
     */
    @GetMapping("/{id}")
    public Result<MerchantVO> getMerchantInfo(@PathVariable Long id) {
        MerchantVO merchantVO = merchantService.getMerchantInfo(id);
        return Result.success(merchantVO);
    }

    /**
     * 获取当前用户的商家信息
     */
    @GetMapping("/info")
    public Result<MerchantVO> getCurrentMerchantInfo(@RequestHeader("X-User-Id") Long userId) {
        MerchantVO merchantVO = merchantService.getMerchantByUserId(userId);
        return Result.success(merchantVO);
    }

    /**
     * 更新商家信息
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateMerchant(@PathVariable Long id,
                                           @RequestHeader("X-User-Id") Long userId,
                                           @RequestBody MerchantApplyRequest request) {
        Boolean result = merchantService.updateMerchant(id, userId, request);
        return Result.success(result);
    }
}
