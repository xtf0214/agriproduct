package com.agriproduct.service;

import com.agriproduct.dto.BannerRequest;
import com.agriproduct.dto.MerchantAuditRequest;
import com.agriproduct.dto.ProductAuditRequest;
import com.agriproduct.dto.UserStatusRequest;
import com.agriproduct.entity.Merchant;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.entity.SysUser;
import com.agriproduct.vo.AdminStatisticsOverviewVO;
import com.agriproduct.vo.CategorySalesVO;
import com.agriproduct.vo.DailyOrderStatisticsVO;
import com.agriproduct.vo.MerchantVO;
import com.agriproduct.vo.ProductVO;
import com.agriproduct.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDate;
import java.util.List;

/**
 * 管理后台服务接口
 */
public interface AdminService {

    // ========== 用户管理 ==========

    IPage<UserInfoVO> getUserList(Page<SysUser> page);

    Boolean updateUserStatus(Long userId, UserStatusRequest request);

    // ========== 商家管理 ==========

    IPage<MerchantVO> getMerchantList(Page<Merchant> page);

    Boolean auditMerchant(Long merchantId, MerchantAuditRequest request);

    // ========== 商品审核 ==========

    IPage<ProductVO> getPendingProducts(Page<ProdProduct> page);

    Boolean auditProduct(Long productId, ProductAuditRequest request);

    // ========== 轮播图管理 ==========

    IPage<com.agriproduct.vo.BannerVO> getBannerList(Page<com.agriproduct.entity.ContentBanner> page);

    Long createBanner(BannerRequest request);

    Boolean updateBanner(Long bannerId, BannerRequest request);

    Boolean deleteBanner(Long bannerId);

    // ========== 统计数据 ==========

    AdminStatisticsOverviewVO getStatisticsOverview();

    List<DailyOrderStatisticsVO> getDailyOrderStatistics(LocalDate startDate, LocalDate endDate);

    List<CategorySalesVO> getCategorySales();
}
