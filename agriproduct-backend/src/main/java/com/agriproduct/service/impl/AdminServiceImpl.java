package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.agriproduct.dto.BannerRequest;
import com.agriproduct.dto.CategoryRequest;
import com.agriproduct.dto.MerchantAuditRequest;
import com.agriproduct.dto.ProductAuditRequest;
import com.agriproduct.dto.UserStatusRequest;
import com.agriproduct.entity.*;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.*;
import com.agriproduct.service.AdminService;
import com.agriproduct.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysUserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final ProdProductMapper productMapper;
    private final ContentBannerMapper bannerMapper;
    private final OrderOrderMapper orderMapper;
    private final ProdCategoryMapper categoryMapper;
    private final OrderItemMapper orderItemMapper;

    // ========== 用户管理 ==========

    @Override
    public IPage<UserInfoVO> getUserList(Page<SysUser> page, String username, String phone) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(SysUser::getUsername, username.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            wrapper.like(SysUser::getPhone, phone.trim());
        }

        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> userPage = userMapper.selectPage(page, wrapper);

        Page<UserInfoVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(userPage.getRecords().stream()
                .map(this::convertUserToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Boolean updateUserStatus(Long userId, UserStatusRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(request.getStatus());
        return userMapper.updateById(user) > 0;
    }

    // ========== 商家管理 ==========

    @Override
    public IPage<MerchantVO> getMerchantList(Page<Merchant> page, String shopName, Integer status) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();

        if (shopName != null && !shopName.trim().isEmpty()) {
            wrapper.like(Merchant::getShopName, shopName.trim());
        }
        if (status != null) {
            wrapper.eq(Merchant::getStatus, status);
        }

        wrapper.orderByDesc(Merchant::getCreateTime);

        IPage<Merchant> merchantPage = merchantMapper.selectPage(page, wrapper);

        Page<MerchantVO> result = new Page<>(merchantPage.getCurrent(), merchantPage.getSize(), merchantPage.getTotal());
        result.setRecords(merchantPage.getRecords().stream()
                .map(this::convertMerchantToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditMerchant(Long merchantId, MerchantAuditRequest request) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家信息不存在");
        }
        if (merchant.getStatus() != 0) {
            throw new BusinessException("该商家已审核，不能重复审核");
        }
        
        merchant.setStatus(request.getAuditStatus());
        merchant.setAuditRemark(request.getAuditRemark());
        
        // 如果审核通过，更新用户类型为商家
        if (request.getAuditStatus() == 1) {
            SysUser user = userMapper.selectById(merchant.getUserId());
            if (user != null) {
                user.setUserType(2);
                userMapper.updateById(user);
            }
        }
        
        return merchantMapper.updateById(merchant) > 0;
    }

    // ========== 商品审核 ==========

    @Override
    public IPage<ProductVO> getPendingProducts(Page<ProdProduct> page) {
        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProdProduct::getAuditStatus, 0)
                .orderByDesc(ProdProduct::getCreateTime);
        
        IPage<ProdProduct> productPage = productMapper.selectPage(page, wrapper);
        
        Page<ProductVO> result = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        result.setRecords(productPage.getRecords().stream()
                .map(this::convertProductToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Boolean auditProduct(Long productId, ProductAuditRequest request) {
        ProdProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        product.setAuditStatus(request.getAuditStatus());
        product.setAuditReason(request.getAuditReason());
        
        if (request.getAuditStatus() == 1) {
            product.setStatus(1);
        }
        
        return productMapper.updateById(product) > 0;
    }

    // ========== 轮播图管理 ==========

    @Override
    public IPage<BannerVO> getBannerList(Page<ContentBanner> page) {
        IPage<ContentBanner> bannerPage = bannerMapper.selectPage(page, 
                new LambdaQueryWrapper<ContentBanner>().orderByAsc(ContentBanner::getSortOrder));
        
        Page<BannerVO> result = new Page<>(bannerPage.getCurrent(), bannerPage.getSize(), bannerPage.getTotal());
        result.setRecords(bannerPage.getRecords().stream()
                .map(this::convertBannerToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Long createBanner(BannerRequest request) {
        ContentBanner banner = BeanUtil.copyProperties(request, ContentBanner.class);
        if (banner.getLinkType() == null) {
            banner.setLinkType(1);
        }
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerMapper.insert(banner);
        return banner.getId();
    }

    @Override
    public Boolean updateBanner(Long bannerId, BannerRequest request) {
        ContentBanner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }

        banner.setTitle(request.getTitle());
        banner.setImage(request.getImage());
        banner.setLinkType(request.getLinkType());
        banner.setLinkId(request.getLinkId());
        banner.setSortOrder(request.getSortOrder());
        banner.setStatus(request.getStatus());
        return bannerMapper.updateById(banner) > 0;
    }

    @Override
    public Boolean deleteBanner(Long bannerId) {
        ContentBanner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        return bannerMapper.deleteById(bannerId) > 0;
    }

    // ========== 分类管理 ==========

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<ProdCategory> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<ProdCategory>()
                        .orderByAsc(ProdCategory::getSortOrder)
        );

        List<ProdCategory> topCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        return topCategories.stream()
                .map(category -> buildCategoryTree(category, allCategories))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getTopCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<ProdCategory>()
                        .eq(ProdCategory::getParentId, 0)
                        .or()
                        .isNull(ProdCategory::getParentId)
                        .orderByAsc(ProdCategory::getSortOrder)
                ).stream()
                .map(this::convertCategoryToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getChildrenCategories(Long parentId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<ProdCategory>()
                        .eq(ProdCategory::getParentId, parentId)
                        .orderByAsc(ProdCategory::getSortOrder)
                ).stream()
                .map(this::convertCategoryToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryRequest request) {
        // 验证分类名称是否重复
        LambdaQueryWrapper<ProdCategory> wrapper = new LambdaQueryWrapper<ProdCategory>()
                .eq(ProdCategory::getName, request.getName().trim());
        
        // 如果指定了父分类，检查父分类是否存在
        if (request.getParentId() != null && request.getParentId() > 0) {
            ProdCategory parent = categoryMapper.selectById(request.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            // 二级分类不能有子分类（只支持两级）
            if (parent.getParentId() != null && parent.getParentId() > 0) {
                throw new BusinessException("不支持创建三级分类，只能在一级分类下创建二级分类");
            }
        }
        
        // 检查同级分类下名称是否重复
        if (request.getParentId() != null && request.getParentId() > 0) {
            wrapper.eq(ProdCategory::getParentId, request.getParentId());
        } else {
            wrapper.and(w -> w.isNull(ProdCategory::getParentId).or().eq(ProdCategory::getParentId, 0));
        }
        
        Long existCount = categoryMapper.selectCount(wrapper);
        if (existCount > 0) {
            throw new BusinessException("同级分类下已存在同名分类");
        }
        
        ProdCategory category = new ProdCategory();
        category.setName(request.getName().trim());
        category.setParentId(request.getParentId() != null && request.getParentId() > 0 ? request.getParentId() : 0L);
        category.setIcon(request.getIcon());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategory(Long categoryId, CategoryRequest request) {
        ProdCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 验证分类名称是否重复（排除自身）
        LambdaQueryWrapper<ProdCategory> wrapper = new LambdaQueryWrapper<ProdCategory>()
                .eq(ProdCategory::getName, request.getName().trim())
                .ne(ProdCategory::getId, categoryId);
        
        if (category.getParentId() != null && category.getParentId() > 0) {
            wrapper.eq(ProdCategory::getParentId, category.getParentId());
        } else {
            wrapper.and(w -> w.isNull(ProdCategory::getParentId).or().eq(ProdCategory::getParentId, 0));
        }
        
        Long existCount = categoryMapper.selectCount(wrapper);
        if (existCount > 0) {
            throw new BusinessException("同级分类下已存在同名分类");
        }
        
        category.setName(request.getName().trim());
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        
        return categoryMapper.updateById(category) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(Long categoryId) {
        ProdCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<ProdCategory>()
                        .eq(ProdCategory::getParentId, categoryId)
        );
        if (childCount > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }
        
        // 检查是否有关联的商品
        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<ProdProduct>()
                        .eq(ProdProduct::getCategoryId, categoryId)
        );
        if (productCount > 0) {
            throw new BusinessException("该分类下存在商品，无法删除");
        }
        
        return categoryMapper.deleteById(categoryId) > 0;
    }

    private CategoryVO buildCategoryTree(ProdCategory category, List<ProdCategory> allCategories) {
        CategoryVO vo = convertCategoryToVO(category);

        List<CategoryVO> children = allCategories.stream()
                .filter(c -> category.getId().equals(c.getParentId()))
                .map(child -> buildCategoryTree(child, allCategories))
                .collect(Collectors.toList());

        vo.setChildren(children);
        vo.setHasChildren(!children.isEmpty());

        return vo;
    }

    private CategoryVO convertCategoryToVO(ProdCategory category) {
        CategoryVO vo = BeanUtil.copyProperties(category, CategoryVO.class);
        vo.setHasChildren(false);
        // BeanUtil.copyProperties 会自动复制 status 字段，如果为 null 则设置默认值 1
        if (vo.getStatus() == null) {
            vo.setStatus(1);
        }
        return vo;
    }

    // ========== 转换方法 ==========

    private UserInfoVO convertUserToVO(SysUser user) {
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }

    private MerchantVO convertMerchantToVO(Merchant merchant) {
        MerchantVO vo = BeanUtil.copyProperties(merchant, MerchantVO.class);
        vo.setStatusDesc(MerchantVO.getStatusDesc(merchant.getStatus()));
        return vo;
    }

    private ProductVO convertProductToVO(ProdProduct product) {
        ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);
        // 设置商家名称
        if (product.getMerchantId() != null) {
            Merchant merchant = merchantMapper.selectById(product.getMerchantId());
            if (merchant != null) {
                vo.setMerchantName(merchant.getShopName());
            }
        }
        // 设置分类名称
        if (product.getCategoryId() != null) {
            ProdCategory category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        return vo;
    }

    private BannerVO convertBannerToVO(ContentBanner banner) {
        return BeanUtil.copyProperties(banner, BannerVO.class);
    }

    // ========== 订单管理 ==========

    @Override
    public IPage<OrderVO> getOrderList(Page<OrderOrder> page, String orderNo, Integer status, Long userId, Long merchantId) {
        LambdaQueryWrapper<OrderOrder> wrapper = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.trim().isEmpty()) {
            wrapper.like(OrderOrder::getOrderNo, orderNo.trim());
        }
        if (status != null) {
            wrapper.eq(OrderOrder::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(OrderOrder::getUserId, userId);
        }
        if (merchantId != null) {
            wrapper.eq(OrderOrder::getMerchantId, merchantId);
        }

        wrapper.orderByDesc(OrderOrder::getCreateTime);

        IPage<OrderOrder> orderPage = orderMapper.selectPage(page, wrapper);

        Page<OrderVO> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        result.setRecords(orderPage.getRecords().stream()
                .map(this::convertOrderToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertOrderToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean shipOrder(Long orderId) {
        OrderOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只有待发货状态的订单才能发货");
        }
        order.setStatus(3);
        order.setShipTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long orderId) {
        OrderOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == 4 || order.getStatus() == 5) {
            throw new BusinessException("已完成或已取消的订单不能取消");
        }
        if (order.getStatus() == 3) {
            throw new BusinessException("已发货的订单不能取消");
        }
        order.setStatus(5);
        return orderMapper.updateById(order) > 0;
    }

    private OrderVO convertOrderToVO(OrderOrder order) {
        OrderVO vo = OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .merchantId(order.getMerchantId())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .statusDesc(OrderVO.getStatusDesc(order.getStatus()))
                .payStatus(order.getPayStatus())
                .payTime(order.getPayTime())
                .shipTime(order.getShipTime())
                .finishTime(order.getFinishTime())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .build();

        // 获取订单项列表
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, order.getId()));
        
        List<OrderItemVO> itemVOs = items.stream()
                .map(this::convertOrderItemToVO)
                .collect(Collectors.toList());
        vo.setItems(itemVOs);

        return vo;
    }

    private OrderItemVO convertOrderItemToVO(OrderItem item) {
        OrderItemVO vo = BeanUtil.copyProperties(item, OrderItemVO.class);
        // 获取商品信息
        ProdProduct product = productMapper.selectById(item.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            vo.setProductImage(product.getMainImage());
        }
        return vo;
    }

    // ========== 统计数据 ==========

    @Override
    public AdminStatisticsOverviewVO getStatisticsOverview() {
        AdminStatisticsOverviewVO overview = new AdminStatisticsOverviewVO();
        
        // 用户总数
        Long totalUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserType, 1));
        overview.setTotalUsers(totalUsers);
        
        // 商家总数
        Long totalMerchants = merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, 1));
        overview.setTotalMerchants(totalMerchants);
        
        // 商品总数
        Long totalProducts = productMapper.selectCount(
                new LambdaQueryWrapper<ProdProduct>().eq(ProdProduct::getStatus, 1));
        overview.setTotalProducts(totalProducts);
        
        // 订单总数
        Long totalOrders = orderMapper.selectCount(null);
        overview.setTotalOrders(totalOrders);
        
        return overview;
    }

    @Override
    public List<DailyOrderStatisticsVO> getDailyOrderStatistics(LocalDate startDate, LocalDate endDate) {
        List<DailyOrderStatisticsVO> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 获取日期范围内的订单
        LambdaQueryWrapper<OrderOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(OrderOrder::getCreateTime, startDate.atStartOfDay())
                .le(OrderOrder::getCreateTime, endDate.atTime(LocalTime.MAX))
                .eq(OrderOrder::getPayStatus, 1);
        
        List<OrderOrder> orders = orderMapper.selectList(wrapper);
        
        // 按日期分组统计
        Map<String, List<OrderOrder>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreateTime().format(formatter)
                ));
        
        // 填充每一天的数据
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateStr = current.format(formatter);
            List<OrderOrder> dayOrders = ordersByDate.getOrDefault(dateStr, new ArrayList<>());
            
            long count = dayOrders.size();
            BigDecimal amount = dayOrders.stream()
                    .map(OrderOrder::getPayAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            result.add(new DailyOrderStatisticsVO(dateStr, count, amount));
            current = current.plusDays(1);
        }
        
        return result;
    }

    @Override
    public List<CategorySalesVO> getCategorySales() {
        List<CategorySalesVO> result = new ArrayList<>();
        
        // 获取所有分类
        List<ProdCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<ProdCategory>()
                        .eq(ProdCategory::getParentId, 0)
                        .orderByAsc(ProdCategory::getSortOrder));
        
        // 统计每个分类的商品数量
        for (ProdCategory category : categories) {
            // 获取该分类下的商品数量
            Long productCount = productMapper.selectCount(
                    new LambdaQueryWrapper<ProdProduct>()
                            .eq(ProdProduct::getCategoryId, category.getId())
                            .eq(ProdProduct::getStatus, 1));
            
            if (productCount > 0) {
                // 计算该分类的销售额（通过订单项）
                List<ProdProduct> categoryProducts = productMapper.selectList(
                        new LambdaQueryWrapper<ProdProduct>()
                                .eq(ProdProduct::getCategoryId, category.getId()));
                
                List<Long> productIds = categoryProducts.stream()
                        .map(ProdProduct::getId)
                        .collect(Collectors.toList());
                
                BigDecimal salesAmount = BigDecimal.ZERO;
                if (!productIds.isEmpty()) {
                    List<OrderItem> orderItems = orderItemMapper.selectList(
                            new LambdaQueryWrapper<OrderItem>()
                                    .in(OrderItem::getProductId, productIds));
                    
                    salesAmount = orderItems.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                
                result.add(new CategorySalesVO(category.getId(), category.getName(), productCount, salesAmount));
            }
        }
        
        return result;
    }
}
