package com.agriproduct.admin.service;

import com.agriproduct.admin.dto.MerchantAuditRequest;
import com.agriproduct.merchant.entity.Merchant;
import com.agriproduct.merchant.vo.MerchantVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 商家管理服务
 */
public interface MerchantManagementService {

    /**
     * 获取商家列表
     */
    IPage<MerchantVO> getMerchantList(Page<Merchant> page);

    /**
     * 审核商家入驻
     * @param merchantId 商家ID
     * @param request 审核请求
     * @return 是否成功
     */
    Boolean auditMerchant(Long merchantId, MerchantAuditRequest request);
}
