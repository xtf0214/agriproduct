package com.agriproduct.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.admin.dto.MerchantAuditRequest;
import com.agriproduct.admin.service.MerchantManagementService;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.merchant.entity.Merchant;
import com.agriproduct.merchant.mapper.MerchantMapper;
import com.agriproduct.merchant.vo.MerchantVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商家管理服务实现
 */
@Service
@RequiredArgsConstructor
public class MerchantManagementServiceImpl implements MerchantManagementService {

    private final MerchantMapper merchantMapper;

    @Override
    public IPage<MerchantVO> getMerchantList(Page<Merchant> page) {
        IPage<Merchant> merchantPage = merchantMapper.selectPage(page, null);

        // 转换为VO
        IPage<MerchantVO> voPage = new Page<>(merchantPage.getCurrent(), merchantPage.getSize(), merchantPage.getTotal());
        voPage.setRecords(merchantPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList()));

        return voPage;
    }

    @Override
    public Boolean auditMerchant(Long merchantId, MerchantAuditRequest request) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }

        // 只能审核待审核的商家
        if (merchant.getStatus() != 0) {
            throw new BusinessException("该商家已审核，不能重复审核");
        }

        merchant.setStatus(request.getAuditStatus());
        merchant.setAuditRemark(request.getAuditRemark());

        return merchantMapper.updateById(merchant) > 0;
    }

    /**
     * 转换为VO
     */
    private MerchantVO convertToVO(Merchant merchant) {
        MerchantVO vo = BeanUtil.copyProperties(merchant, MerchantVO.class);
        vo.setStatusDesc(MerchantVO.getStatusDesc(merchant.getStatus()));
        return vo;
    }
}
