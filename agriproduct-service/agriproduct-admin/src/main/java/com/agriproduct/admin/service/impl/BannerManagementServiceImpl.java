package com.agriproduct.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.admin.service.BannerManagementService;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.product.entity.ContentBanner;
import com.agriproduct.product.mapper.ContentBannerMapper;
import com.agriproduct.product.vo.BannerVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮播图管理服务实现
 */
@Service
@RequiredArgsConstructor
public class BannerManagementServiceImpl implements BannerManagementService {

    private final ContentBannerMapper bannerMapper;

    @Override
    public IPage<BannerVO> getBannerList(Page<ContentBanner> page) {
        IPage<ContentBanner> bannerPage = bannerMapper.selectPage(page, null);

        // 转换为VO
        IPage<BannerVO> voPage = new Page<>(bannerPage.getCurrent(), bannerPage.getSize(), bannerPage.getTotal());
        voPage.setRecords(bannerPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public List<BannerVO> getAllBanners() {
        return bannerMapper.selectList(null).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean addBanner(ContentBanner banner) {
        return bannerMapper.insert(banner) > 0;
    }

    @Override
    public Boolean updateBanner(ContentBanner banner) {
        ContentBanner existing = bannerMapper.selectById(banner.getId());
        if (existing == null) {
            throw new BusinessException("轮播图不存在");
        }
        return bannerMapper.updateById(banner) > 0;
    }

    @Override
    public Boolean deleteBanner(Long bannerId) {
        return bannerMapper.deleteById(bannerId) > 0;
    }

    /**
     * 转换为VO
     */
    private BannerVO convertToVO(ContentBanner banner) {
        return BeanUtil.copyProperties(banner, BannerVO.class);
    }
}
