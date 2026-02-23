package com.agriproduct.service;

import com.agriproduct.dto.MerchantApplyRequest;
import com.agriproduct.entity.Merchant;
import com.agriproduct.vo.MerchantVO;
import com.agriproduct.vo.ProductSalesVO;
import com.agriproduct.vo.SalesStatisticsVO;
import com.agriproduct.vo.StatisticsOverviewVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家服务接口
 */
public interface MerchantService extends IService<Merchant> {

    Long applyForMerchant(Long userId, MerchantApplyRequest request);

    MerchantVO getMerchantInfo(Long merchantId);

    MerchantVO getMerchantByUserId(Long userId);

    Boolean updateMerchant(Long merchantId, Long userId, MerchantApplyRequest request);

    StatisticsOverviewVO getStatisticsOverview(Long merchantId);

    List<SalesStatisticsVO> getSalesStatistics(Long merchantId, LocalDate startDate, LocalDate endDate);

    List<ProductSalesVO> getProductSalesRank(Long merchantId, Integer limit);
}
