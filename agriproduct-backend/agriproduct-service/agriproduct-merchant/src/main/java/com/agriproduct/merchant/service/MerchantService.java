package com.agriproduct.merchant.service;

import com.agriproduct.merchant.dto.MerchantApplyRequest;
import com.agriproduct.merchant.vo.MerchantVO;

/**
 * 商家服务接口
 */
public interface MerchantService {

    /**
     * 商家入驻申请
     * @param userId 用户ID
     * @param request 申请信息
     * @return 商家ID
     */
    Long applyForMerchant(Long userId, MerchantApplyRequest request);

    /**
     * 获取商家信息
     * @param merchantId 商家ID
     * @return 商家信息
     */
    MerchantVO getMerchantInfo(Long merchantId);

    /**
     * 获取用户关联的商家信息
     * @param userId 用户ID
     * @return 商家信息
     */
    MerchantVO getMerchantByUserId(Long userId);

    /**
     * 更新商家信息
     * @param merchantId 商家ID
     * @param userId 用户ID
     * @param request 更新信息
     * @return 是否成功
     */
    Boolean updateMerchant(Long merchantId, Long userId, MerchantApplyRequest request);
}
