package com.agriproduct.service;

import com.agriproduct.dto.OrderCreateRequest;
import com.agriproduct.entity.OrderOrder;
import com.agriproduct.vo.OrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<OrderOrder> {

    OrderVO createOrder(Long userId, OrderCreateRequest request);

    OrderVO getOrderDetail(Long orderId);

    IPage<OrderVO> getUserOrders(Page<OrderOrder> page, Long userId, Integer status);

    IPage<OrderVO> getMerchantOrders(Page<OrderOrder> page, Long merchantId);

    void cancelOrder(Long orderId, Long userId);

    void confirmOrder(Long orderId, Long userId);

    void shipOrder(Long orderId, Long merchantId);

    void payOrder(Long orderId, Long userId);
}
