package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.agriproduct.dto.MerchantApplyRequest;
import com.agriproduct.entity.Merchant;
import com.agriproduct.entity.OrderOrder;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.MerchantMapper;
import com.agriproduct.mapper.OrderOrderMapper;
import com.agriproduct.mapper.ProdProductMapper;
import com.agriproduct.service.MerchantService;
import com.agriproduct.vo.MerchantVO;
import com.agriproduct.vo.ProductSalesVO;
import com.agriproduct.vo.SalesStatisticsVO;
import com.agriproduct.vo.StatisticsOverviewVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final OrderOrderMapper orderOrderMapper;
    private final ProdProductMapper prodProductMapper;

    @Override
    public Long applyForMerchant(Long userId, MerchantApplyRequest request) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        Merchant existingMerchant = getOne(wrapper);

        if (existingMerchant != null) {
            if (existingMerchant.getStatus() == 1) {
                throw new BusinessException("您已经是认证商家，无需重复申请");
            } else if (existingMerchant.getStatus() == 0) {
                throw new BusinessException("您的申请正在审核中，请耐心等待");
            } else {
                throw new BusinessException("您的申请已被拒绝，如需重新申请请联系客服");
            }
        }

        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        merchant.setShopName(request.getShopName());
        merchant.setShopDesc(request.getShopDesc());
        merchant.setBusinessLicense(request.getBusinessLicense());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setContactName(request.getContactName());
        merchant.setStatus(0);
        save(merchant);
        
        return merchant.getId();
    }

    @Override
    public MerchantVO getMerchantInfo(Long merchantId) {
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        return convertToVO(merchant);
    }

    @Override
    public MerchantVO getMerchantByUserId(Long userId) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        Merchant merchant = getOne(wrapper);
        if (merchant == null) {
            throw new BusinessException("您还不是商家，请先提交入驻申请");
        }
        return convertToVO(merchant);
    }

    @Override
    public Boolean updateMerchant(Long merchantId, Long userId, MerchantApplyRequest request) {
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        if (!userId.equals(merchant.getUserId())) {
            throw new BusinessException("无权修改此商家信息");
        }
        
        merchant.setShopName(request.getShopName());
        merchant.setShopDesc(request.getShopDesc());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setContactName(request.getContactName());
        
        return updateById(merchant);
    }

            @Override
            public StatisticsOverviewVO getStatisticsOverview(Long merchantId) {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

            List<OrderOrder> todayPaidOrders = orderOrderMapper.selectList(new LambdaQueryWrapper<OrderOrder>()
                .eq(OrderOrder::getMerchantId, merchantId)
                .eq(OrderOrder::getPayStatus, 1)
                .ge(OrderOrder::getCreateTime, startOfDay)
                .lt(OrderOrder::getCreateTime, endOfDay));

            BigDecimal todaySales = todayPaidOrders.stream()
                .map(OrderOrder::getPayAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Long todayOrders = (long) todayPaidOrders.size();

            Long totalProducts = prodProductMapper.selectCount(new LambdaQueryWrapper<ProdProduct>()
                .eq(ProdProduct::getMerchantId, merchantId)
                .eq(ProdProduct::getStatus, 1));

            Long pendingOrders = orderOrderMapper.selectCount(new LambdaQueryWrapper<OrderOrder>()
                .eq(OrderOrder::getMerchantId, merchantId)
                .eq(OrderOrder::getStatus, 2));

            StatisticsOverviewVO overview = new StatisticsOverviewVO();
            overview.setTodaySales(todaySales);
            overview.setTodayOrders(todayOrders);
            overview.setTotalProducts(totalProducts);
            overview.setPendingOrders(pendingOrders);
            return overview;
            }

            @Override
            public List<SalesStatisticsVO> getSalesStatistics(Long merchantId, LocalDate startDate, LocalDate endDate) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.plusDays(1).atStartOfDay();

            List<OrderOrder> orders = orderOrderMapper.selectList(new LambdaQueryWrapper<OrderOrder>()
                .eq(OrderOrder::getMerchantId, merchantId)
                .eq(OrderOrder::getPayStatus, 1)
                .ge(OrderOrder::getCreateTime, start)
                .lt(OrderOrder::getCreateTime, end)
                .orderByAsc(OrderOrder::getCreateTime));

            Map<LocalDate, List<OrderOrder>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getCreateTime().toLocalDate()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<SalesStatisticsVO> result = new ArrayList<>();
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                List<OrderOrder> dateOrders = groupedByDate.getOrDefault(current, List.of());
                BigDecimal amount = dateOrders.stream()
                    .map(OrderOrder::getPayAmount)
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                long count = dateOrders.size();
                result.add(new SalesStatisticsVO(current.format(formatter), amount, count));
                current = current.plusDays(1);
            }
            return result;
            }

            @Override
            public List<ProductSalesVO> getProductSalesRank(Long merchantId, Integer limit) {
            int safeLimit = (limit == null || limit <= 0) ? 10 : limit;

            List<ProdProduct> products = prodProductMapper.selectList(new LambdaQueryWrapper<ProdProduct>()
                .eq(ProdProduct::getMerchantId, merchantId)
                .orderByDesc(ProdProduct::getSales)
                .last("LIMIT " + safeLimit));

            return products.stream().map(product -> {
                ProductSalesVO vo = new ProductSalesVO();
                vo.setProductId(product.getId());
                vo.setProductName(product.getName());
                vo.setProductImage(product.getMainImage());
                vo.setSalesCount(product.getSales() == null ? 0 : product.getSales());
                BigDecimal price = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
                int sales = product.getSales() == null ? 0 : product.getSales();
                vo.setSalesAmount(price.multiply(BigDecimal.valueOf(sales)));
                return vo;
            }).collect(Collectors.toList());
            }

    private MerchantVO convertToVO(Merchant merchant) {
        MerchantVO vo = BeanUtil.copyProperties(merchant, MerchantVO.class);
        vo.setStatusDesc(MerchantVO.getStatusDesc(merchant.getStatus()));
        return vo;
    }
}
