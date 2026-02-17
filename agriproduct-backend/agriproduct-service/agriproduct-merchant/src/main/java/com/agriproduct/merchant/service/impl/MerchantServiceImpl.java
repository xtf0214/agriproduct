package com.agriproduct.merchant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.merchant.dto.MerchantApplyRequest;
import com.agriproduct.merchant.entity.Merchant;
import com.agriproduct.merchant.mapper.MerchantMapper;
import com.agriproduct.merchant.service.MerchantService;
import com.agriproduct.merchant.vo.MerchantVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商家服务实现
 */
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    @Override
    public Long applyForMerchant(Long userId, MerchantApplyRequest request) {
        // 检查用户是否已经是商家
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        Merchant existingMerchant = getOne(wrapper);

        if (existingMerchant != null) {
            if (existingMerchant.getStatus() == 1) {
                throw new BusinessException("您已经是认证商家，无需重复申请");
            } else if (existingMerchant.getStatus() == 0) {
                throw new BusinessException("您的申请正在审核中，请耐心等待");
            } else {
                throw new BusinessException("您的申请已被拒绝，如需重新申请请联系客服");
            }
        }

        // 创建商家申请
        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        merchant.setShopName(request.getShopName());
        merchant.setShopDesc(request.getShopDesc());
        merchant.setBusinessLicense(request.getBusinessLicense());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setContactName(request.getContactName());
        merchant.setStatus(0); // 待审核

        save(merchant);
        return merchant.getId();
    }

    @Override
    public MerchantVO getMerchantInfo(Long merchantId) {
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        return convertToVO(merchant);
    }

    @Override
    public MerchantVO getMerchantByUserId(Long userId) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        Merchant merchant = getOne(wrapper);

        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        return convertToVO(merchant);
    }

    @Override
    public Boolean updateMerchant(Long merchantId, Long userId, MerchantApplyRequest request) {
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }

        // 权限校验：只能修改自己的商家信息
        if (!merchant.getUserId().equals(userId)) {
            throw new BusinessException("无权修改其他商家的信息");
        }

        // 只有审核通过才能修改基本信息
        if (merchant.getStatus() != 1) {
            throw new BusinessException("只有审核通过的商家才能修改信息");
        }

        // 更新可修改的字段
        if (request.getShopName() != null) {
            merchant.setShopName(request.getShopName());
        }
        if (request.getShopDesc() != null) {
            merchant.setShopDesc(request.getShopDesc());
        }
        if (request.getContactPhone() != null) {
            merchant.setContactPhone(request.getContactPhone());
        }
        if (request.getContactName() != null) {
            merchant.setContactName(request.getContactName());
        }

        return updateById(merchant);
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
