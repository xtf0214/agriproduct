package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.entity.ContentBanner;
import com.agriproduct.mapper.ContentBannerMapper;
import com.agriproduct.service.BannerService;
import com.agriproduct.vo.BannerVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl extends ServiceImpl<ContentBannerMapper, ContentBanner> implements BannerService {

    @Override
    public List<BannerVO> getActiveBanners() {
        return list(new LambdaQueryWrapper<ContentBanner>()
                        .eq(ContentBanner::getStatus, 1)
                        .orderByAsc(ContentBanner::getSortOrder)
                ).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private BannerVO convertToVO(ContentBanner banner) {
        return BeanUtil.copyProperties(banner, BannerVO.class);
    }
}
