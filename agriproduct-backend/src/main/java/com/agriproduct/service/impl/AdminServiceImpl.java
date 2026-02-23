package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.agriproduct.dto.BannerRequest;
import com.agriproduct.dto.MerchantAuditRequest;
import com.agriproduct.dto.ProductAuditRequest;
import com.agriproduct.dto.UserStatusRequest;
import com.agriproduct.entity.*;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.*;
import com.agriproduct.service.AdminService;
import com.agriproduct.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysUserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final ProdProductMapper productMapper;
    private final ContentBannerMapper bannerMapper;

    // ========== 用户管理 ==========

    @Override
    public IPage<UserInfoVO> getUserList(Page<SysUser> page) {
        IPage<SysUser> userPage = userMapper.selectPage(page, null);
        
        Page<UserInfoVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(userPage.getRecords().stream()
                .map(this::convertUserToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Boolean updateUserStatus(Long userId, UserStatusRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(request.getStatus());
        return userMapper.updateById(user) > 0;
    }

    // ========== 商家管理 ==========

    @Override
    public IPage<MerchantVO> getMerchantList(Page<Merchant> page) {
        IPage<Merchant> merchantPage = merchantMapper.selectPage(page, null);
        
        Page<MerchantVO> result = new Page<>(merchantPage.getCurrent(), merchantPage.getSize(), merchantPage.getTotal());
        result.setRecords(merchantPage.getRecords().stream()
                .map(this::convertMerchantToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditMerchant(Long merchantId, MerchantAuditRequest request) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        if (merchant.getStatus() != 0) {
            throw new BusinessException("该商家已审核，不能重复审核");
        }
        
        merchant.setStatus(request.getAuditStatus());
        merchant.setAuditRemark(request.getAuditRemark());
        
        // 如果审核通过，更新用户类型为商家
        if (request.getAuditStatus() == 1) {
            SysUser user = userMapper.selectById(merchant.getUserId());
            if (user != null) {
                user.setUserType(2);
                userMapper.updateById(user);
            }
        }
        
        return merchantMapper.updateById(merchant) > 0;
    }

    // ========== 商品审核 ==========

    @Override
    public IPage<ProductVO> getPendingProducts(Page<ProdProduct> page) {
        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProdProduct::getAuditStatus, 0)
                .orderByDesc(ProdProduct::getCreateTime);
        
        IPage<ProdProduct> productPage = productMapper.selectPage(page, wrapper);
        
        Page<ProductVO> result = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        result.setRecords(productPage.getRecords().stream()
                .map(this::convertProductToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Boolean auditProduct(Long productId, ProductAuditRequest request) {
        ProdProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        product.setAuditStatus(request.getAuditStatus());
        product.setAuditReason(request.getAuditReason());
        
        if (request.getAuditStatus() == 1) {
            product.setStatus(1);
        }
        
        return productMapper.updateById(product) > 0;
    }

    // ========== 轮播图管理 ==========

    @Override
    public IPage<BannerVO> getBannerList(Page<ContentBanner> page) {
        IPage<ContentBanner> bannerPage = bannerMapper.selectPage(page, 
                new LambdaQueryWrapper<ContentBanner>().orderByAsc(ContentBanner::getSortOrder));
        
        Page<BannerVO> result = new Page<>(bannerPage.getCurrent(), bannerPage.getSize(), bannerPage.getTotal());
        result.setRecords(bannerPage.getRecords().stream()
                .map(this::convertBannerToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Long createBanner(BannerRequest request) {
        ContentBanner banner = BeanUtil.copyProperties(request, ContentBanner.class);
        if (banner.getLinkType() == null) {
            banner.setLinkType(1);
        }
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerMapper.insert(banner);
        return banner.getId();
    }

    @Override
    public Boolean updateBanner(Long bannerId, BannerRequest request) {
        ContentBanner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }

        banner.setTitle(request.getTitle());
        banner.setImage(request.getImage());
        banner.setLinkType(request.getLinkType());
        banner.setLinkId(request.getLinkId());
        banner.setSortOrder(request.getSortOrder());
        banner.setStatus(request.getStatus());
        return bannerMapper.updateById(banner) > 0;
    }

    @Override
    public Boolean deleteBanner(Long bannerId) {
        ContentBanner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        return bannerMapper.deleteById(bannerId) > 0;
    }

    // ========== 转换方法 ==========

    private UserInfoVO convertUserToVO(SysUser user) {
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }

    private MerchantVO convertMerchantToVO(Merchant merchant) {
        MerchantVO vo = BeanUtil.copyProperties(merchant, MerchantVO.class);
        vo.setStatusDesc(MerchantVO.getStatusDesc(merchant.getStatus()));
        return vo;
    }

    private ProductVO convertProductToVO(ProdProduct product) {
        ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);
        return vo;
    }

    private BannerVO convertBannerToVO(ContentBanner banner) {
        return BeanUtil.copyProperties(banner, BannerVO.class);
    }
}
