package com.agriproduct.admin.service;

import com.agriproduct.product.entity.ContentBanner;
import com.agriproduct.product.vo.BannerVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 轮播图管理服务
 */
public interface BannerManagementService {

    /**
     * 获取轮播图列表
     */
    IPage<BannerVO> getBannerList(Page<ContentBanner> page);

    /**
     * 获取所有轮播图（用于选择）
     */
    List<BannerVO> getAllBanners();

    /**
     * 添加轮播图
     * @param banner 轮播图信息
     * @return 是否成功
     */
    Boolean addBanner(ContentBanner banner);

    /**
     * 更新轮播图
     * @param banner 轮播图信息
     * @return 是否成功
     */
    Boolean updateBanner(ContentBanner banner);

    /**
     * 删除轮播图
     * @param bannerId 轮播图ID
     * @return 是否成功
     */
    Boolean deleteBanner(Long bannerId);
}
