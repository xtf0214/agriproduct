package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;

import com.agriproduct.dto.OrderCreateRequest;
import com.agriproduct.entity.*;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.*;
import com.agriproduct.service.OrderService;
import com.agriproduct.vo.OrderItemVO;
import com.agriproduct.vo.OrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderOrderMapper, OrderOrder> implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final CartShoppingMapper cartMapper;
    private final ProdProductMapper productMapper;
    private final UserAddressMapper addressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(Long userId, OrderCreateRequest request) {
        UserAddress address = addressMapper.selectById(request.getAddressId());
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        // 从购物车创建订单
        if (request.getCartIds() == null || request.getCartIds().isEmpty()) {
            throw new BusinessException("请选择要购买的商品");
        }
        
        List<CartShopping> carts = cartMapper.selectBatchIds(request.getCartIds());
        Long merchantId = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 第一轮遍历：验证商品并计算总金额
        for (CartShopping cart : carts) {
            if (!userId.equals(cart.getUserId())) {
                throw new BusinessException("购物车数据异常");
            }
            
            ProdProduct product = productMapper.selectById(cart.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            if (product.getStatus() != 1) {
                throw new BusinessException("商品[" + product.getName() + "]已下架");
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足");
            }
            
            if (merchantId == null) {
                merchantId = product.getMerchantId();
            }
            
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // 创建并保存订单
        OrderOrder order = new OrderOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(1);
        order.setPayStatus(0);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + 
                address.getDistrict() + address.getDetailAddress());
        order.setRemark(request.getRemark());
        save(order);
        
        // 第二轮遍历：创建订单项并更新库存
        for (CartShopping cart : carts) {
            ProdProduct product = productMapper.selectById(cart.getProductId());
            
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalAmount(itemTotal);
            orderItemMapper.insert(item);

            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateById(product);
        }
        
        cartMapper.deleteBatchIds(request.getCartIds());

        return getOrderDetail(order.getId());
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );

        return convertToVO(order, items);
    }

    @Override
    public IPage<OrderVO> getUserOrders(Page<OrderOrder> page, Long userId, Integer status) {
        LambdaQueryWrapper<OrderOrder> wrapper = new LambdaQueryWrapper<OrderOrder>()
                .eq(OrderOrder::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(OrderOrder::getStatus, status);
        }
        
        wrapper.orderByDesc(OrderOrder::getCreateTime);
        
        IPage<OrderOrder> orderPage = page(page, wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectList(
                            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
                    );
                    return convertToVO(order, items);
                })
                .collect(Collectors.toList());

        Page<OrderVO> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public IPage<OrderVO> getMerchantOrders(Page<OrderOrder> page, Long merchantId) {
        IPage<OrderOrder> orderPage = page(
                page,
                new LambdaQueryWrapper<OrderOrder>()
                        .eq(OrderOrder::getMerchantId, merchantId)
                        .orderByDesc(OrderOrder::getCreateTime)
        );

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectList(
                            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
                    );
                    return convertToVO(order, items);
                })
                .collect(Collectors.toList());

        Page<OrderVO> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只有待付款订单才能取消");
        }
        order.setStatus(5);
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId, Long userId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("只有待收货订单才能确认收货");
        }
        order.setStatus(4);
        order.setFinishTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Long userId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只有待付款订单才能支付");
        }
        order.setStatus(2);
        order.setPayStatus(1);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId, Long merchantId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!merchantId.equals(order.getMerchantId())) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只有待发货订单才能发货");
        }
        order.setStatus(3);
        order.setShipTime(LocalDateTime.now());
        updateById(order);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + IdUtil.getSnowflake(1, 1).nextId();
    }

    private OrderVO convertToVO(OrderOrder order, List<OrderItem> items) {
        OrderVO vo = BeanUtil.copyProperties(order, OrderVO.class);
        vo.setStatusDesc(OrderVO.getStatusDesc(order.getStatus()));
        
        List<OrderItemVO> itemVOList = items.stream()
                .map(item -> BeanUtil.copyProperties(item, OrderItemVO.class))
                .collect(Collectors.toList());
        vo.setItems(itemVOList);
        
        return vo;
    }
}
