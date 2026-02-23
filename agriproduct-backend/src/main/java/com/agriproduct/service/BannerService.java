package com.agriproduct.service;

import com.agriproduct.entity.ContentBanner;
import com.agriproduct.vo.BannerVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService extends IService<ContentBanner> {

    List<BannerVO> getActiveBanners();
}
