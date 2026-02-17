package com.agriproduct.product.service;

import com.agriproduct.product.entity.ContentBanner;
import com.agriproduct.product.vo.BannerVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService extends IService<ContentBanner> {

    /**
     * 获取启用的轮播图列表
     */
    List<BannerVO> getActiveBanners();
}
