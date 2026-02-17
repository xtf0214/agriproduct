package com.agriproduct.order.service;

import com.agriproduct.order.dto.OrderCreateRequest;
import com.agriproduct.order.entity.OrderOrder;
import com.agriproduct.order.vo.OrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<OrderOrder> {

    /**
     * 创建订单
     */
    OrderVO createOrder(Long userId, OrderCreateRequest request);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 获取用户订单列表
     */
    IPage<OrderVO> getUserOrders(Page<OrderOrder> page, Long userId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 确认收货
     */
    void confirmOrder(Long orderId);

    /**
     * 订单发货（商家端）
     */
    void shipOrder(Long orderId);
}
