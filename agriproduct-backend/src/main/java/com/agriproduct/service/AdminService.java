package com.agriproduct.service;

import com.agriproduct.dto.BannerRequest;
import com.agriproduct.dto.CategoryRequest;
import com.agriproduct.dto.MerchantAuditRequest;
import com.agriproduct.dto.ProductAuditRequest;
import com.agriproduct.dto.UserStatusRequest;
import com.agriproduct.entity.Merchant;
import com.agriproduct.entity.OrderOrder;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.entity.SysUser;
import com.agriproduct.vo.AdminStatisticsOverviewVO;
import com.agriproduct.vo.CategorySalesVO;
import com.agriproduct.vo.CategoryVO;
import com.agriproduct.vo.DailyOrderStatisticsVO;
import com.agriproduct.vo.MerchantVO;
import com.agriproduct.vo.OrderVO;
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

    IPage<UserInfoVO> getUserList(Page<SysUser> page, String username, String phone);

    Boolean updateUserStatus(Long userId, UserStatusRequest request);

    // ========== 商家管理 ==========

    IPage<MerchantVO> getMerchantList(Page<Merchant> page, String shopName, Integer status);

    Boolean auditMerchant(Long merchantId, MerchantAuditRequest request);

    // ========== 商品审核 ==========

    IPage<ProductVO> getPendingProducts(Page<ProdProduct> page);

    Boolean auditProduct(Long productId, ProductAuditRequest request);

    // ========== 轮播图管理 ==========

    IPage<com.agriproduct.vo.BannerVO> getBannerList(Page<com.agriproduct.entity.ContentBanner> page);

    Long createBanner(BannerRequest request);

    Boolean updateBanner(Long bannerId, BannerRequest request);

    Boolean deleteBanner(Long bannerId);

    // ========== 分类管理 ==========

    /**
     * 获取分类列表（树形结构）
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取一级分类列表
     */
    List<CategoryVO> getTopCategories();

    /**
     * 获取某个父分类下的子分类
     */
    List<CategoryVO> getChildrenCategories(Long parentId);

    /**
     * 创建分类（一级或二级）
     */
    Long createCategory(CategoryRequest request);

    /**
     * 更新分类
     */
    Boolean updateCategory(Long categoryId, CategoryRequest request);

    /**
     * 删除分类
     */
    Boolean deleteCategory(Long categoryId);

    // ========== 订单管理 ==========

    /**
     * 获取订单列表（分页）
     * @param page 分页参数
     * @param orderNo 订单号（可选）
     * @param status 订单状态（可选）
     * @param userId 用户ID（可选）
     * @param merchantId 商家ID（可选）
     * @return 订单列表
     */
    IPage<OrderVO> getOrderList(Page<OrderOrder> page, String orderNo, Integer status, Long userId, Long merchantId);

    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 发货
     * @param orderId 订单ID
     * @return 是否成功
     */
    Boolean shipOrder(Long orderId);

    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 是否成功
     */
    Boolean cancelOrder(Long orderId);

    // ========== 统计数据 ==========

    AdminStatisticsOverviewVO getStatisticsOverview();

    List<DailyOrderStatisticsVO> getDailyOrderStatistics(LocalDate startDate, LocalDate endDate);

    List<CategorySalesVO> getCategorySales();
}
