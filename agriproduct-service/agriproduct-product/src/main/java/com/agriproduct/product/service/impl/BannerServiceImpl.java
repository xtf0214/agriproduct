package com.agriproduct.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.product.entity.ContentBanner;
import com.agriproduct.product.mapper.ContentBannerMapper;
import com.agriproduct.product.service.BannerService;
import com.agriproduct.product.vo.BannerVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮播图服务实现
 */
@Service
@RequiredArgsConstructor
public class BannerServiceImpl extends ServiceImpl<ContentBannerMapper, ContentBanner> implements BannerService {

    @Override
    public List<BannerVO> getActiveBanners() {
        return list(new LambdaQueryWrapper<ContentBanner>()
                        .eq(ContentBanner::getStatus, 1)  // 启用状态
                        .orderByAsc(ContentBanner::getSortOrder)
                ).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private BannerVO convertToVO(ContentBanner banner) {
        return BeanUtil.copyProperties(banner, BannerVO.class);
    }
}
