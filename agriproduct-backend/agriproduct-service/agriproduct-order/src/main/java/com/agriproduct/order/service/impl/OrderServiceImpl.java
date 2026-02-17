package com.agriproduct.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.order.dto.OrderCreateRequest;
import com.agriproduct.order.entity.OrderItem;
import com.agriproduct.order.entity.OrderOrder;
import com.agriproduct.order.mapper.OrderItemMapper;
import com.agriproduct.order.mapper.OrderOrderMapper;
import com.agriproduct.order.service.OrderService;
import com.agriproduct.order.vo.OrderItemVO;
import com.agriproduct.order.vo.OrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderOrderMapper, OrderOrder> implements OrderService {

    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(Long userId, OrderCreateRequest request) {
        // TODO: 获取收货地址信息
        // TODO: 获取商品信息和库存校验

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        OrderOrder order = new OrderOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus(1); // 待付款
        order.setPayStatus(0); // 未支付
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        // TODO: 设置实际地址信息
        order.setReceiverName("收货人");
        order.setReceiverPhone("13800138000");
        order.setReceiverAddress("收货地址");
        order.setRemark(request.getRemark());

        save(order);

        // TODO: 创建订单商品明细
        // TODO: 扣减库存

        return getOrderDetail(order.getId());
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 获取订单商品明细
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );

        return convertToVO(order, items);
    }

    @Override
    public IPage<OrderVO> getUserOrders(Page<OrderOrder> page, Long userId) {
        IPage<OrderOrder> orderPage = page(
                page,
                new LambdaQueryWrapper<OrderOrder>()
                        .eq(OrderOrder::getUserId, userId)
                        .orderByDesc(OrderOrder::getCreateTime)
        );

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectList(
                            new LambdaQueryWrapper<OrderItem>()
                                    .eq(OrderItem::getOrderId, order.getId())
                    );
                    return convertToVO(order, items);
                })
                .collect(Collectors.toList());

        // 构建新的分页结果
        Page<OrderVO> resultPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只有待付款订单才能取消");
        }
        order.setStatus(5); // 已取消
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("只有待收货订单才能确认收货");
        }
        order.setStatus(4); // 已完成
        order.setFinishTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId) {
        OrderOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只有待发货订单才能发货");
        }
        order.setStatus(3); // 待收货
        order.setShipTime(LocalDateTime.now());
        updateById(order);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + IdUtil.getSnowflake(1, 1).nextId();
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(OrderOrder order, List<OrderItem> items) {
        OrderVO vo = BeanUtil.copyProperties(order, OrderVO.class);
        vo.setStatusDesc(getStatusDesc(order.getStatus()));
        vo.setPayStatusDesc(getPayStatusDesc(order.getPayStatus()));

        List<OrderItemVO> itemVOList = items.stream()
                .map(this::convertItemToVO)
                .collect(Collectors.toList());
        vo.setItems(itemVOList);

        return vo;
    }

    /**
     * 转换订单商品为VO
     */
    private OrderItemVO convertItemToVO(OrderItem item) {
        return BeanUtil.copyProperties(item, OrderItemVO.class);
    }

    /**
     * 获取订单状态描述
     */
    private String getStatusDesc(Integer status) {
        return switch (status) {
            case 1 -> "待付款";
            case 2 -> "待发货";
            case 3 -> "待收货";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
    }

    /**
     * 获取支付状态描述
     */
    private String getPayStatusDesc(Integer payStatus) {
        return payStatus == 1 ? "已支付" : "未支付";
    }
}
