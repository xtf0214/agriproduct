package com.agriproduct.order.service;

import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.order.entity.OrderItem;
import com.agriproduct.order.entity.OrderOrder;
import com.agriproduct.order.mapper.OrderItemMapper;
import com.agriproduct.order.mapper.OrderOrderMapper;
import com.agriproduct.order.service.impl.OrderServiceImpl;
import com.agriproduct.order.vo.OrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("订单服务测试")
class OrderServiceTest {

    @Mock
    private OrderOrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射注入 baseMapper (InjectMocks不会注入父类的字段)
        Field baseMapperField = orderService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(orderService, orderMapper);
    }

    private OrderOrder createTestOrder(Long id, String orderNo, Long userId, Integer status, Integer payStatus) {
        OrderOrder order = new OrderOrder();
        order.setId(id);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMerchantId(1L);
        order.setTotalAmount(BigDecimal.valueOf(100.00));
        order.setPayAmount(BigDecimal.valueOf(100.00));
        order.setStatus(status);
        order.setPayStatus(payStatus);
        order.setReceiverName("张三");
        order.setReceiverPhone("13900139000");
        order.setReceiverAddress("北京市朝阳区xx街道xx号");
        order.setCreateTime(LocalDateTime.now());
        return order;
    }

    @Test
    @DisplayName("获取订单详情成功")
    void testGetOrderDetailSuccess() {
        // 准备测试数据
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 2, 1);

        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setOrderId(orderId);
        item1.setProductId(100L);
        item1.setProductName("有机菠菜");
        item1.setPrice(BigDecimal.valueOf(8.00));
        item1.setQuantity(2);
        item1.setTotalAmount(BigDecimal.valueOf(16.00));

        when(orderMapper.selectById(orderId)).thenReturn(order);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(item1));

        // 执行测试
        OrderVO result = orderService.getOrderDetail(orderId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        assertThat(result.getOrderNo()).isEqualTo("ORD001");
        assertThat(result.getStatus()).isEqualTo(2);
        assertThat(result.getStatusDesc()).isEqualTo("待发货");
        assertThat(result.getItems()).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductName()).isEqualTo("有机菠菜");
    }

    @Test
    @DisplayName("获取订单详情失败 - 订单不存在")
    void testGetOrderDetailNotFound() {
        // 模拟订单不存在
        when(orderMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        assertThatThrownBy(() -> orderService.getOrderDetail(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("订单不存在");
    }

    @Test
    @DisplayName("取消订单成功")
    void testCancelOrderSuccess() {
        // 准备测试数据
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 1, 0); // 待付款状态

        when(orderMapper.selectById(orderId)).thenReturn(order);
        when(orderMapper.updateById(any(OrderOrder.class))).thenReturn(1);

        // 执行测试
        orderService.cancelOrder(orderId);

        // 验证
        verify(orderMapper).updateById(any(OrderOrder.class));
    }

    @Test
    @DisplayName("取消订单失败 - 订单状态不允许取消")
    void testCancelOrderInvalidStatus() {
        // 准备测试数据 - 已发货订单
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 3, 1); // 待收货状态

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // 执行测试并验证异常
        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("只有待付款订单才能取消");
    }

    @Test
    @DisplayName("确认收货成功")
    void testConfirmOrderSuccess() {
        // 准备测试数据
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 3, 1); // 待收货状态

        when(orderMapper.selectById(orderId)).thenReturn(order);
        when(orderMapper.updateById(any(OrderOrder.class))).thenReturn(1);

        // 执行测试
        orderService.confirmOrder(orderId);

        // 验证
        verify(orderMapper).updateById(any(OrderOrder.class));
    }

    @Test
    @DisplayName("确认收货失败 - 订单状态不允许确认")
    void testConfirmOrderInvalidStatus() {
        // 准备测试数据 - 待付款订单
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 1, 0); // 待付款状态

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // 执行测试并验证异常
        assertThatThrownBy(() -> orderService.confirmOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("只有待收货订单才能确认收货");
    }

    @Test
    @DisplayName("订单发货成功")
    void testShipOrderSuccess() {
        // 准备测试数据
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 2, 1); // 待发货状态

        when(orderMapper.selectById(orderId)).thenReturn(order);
        when(orderMapper.updateById(any(OrderOrder.class))).thenReturn(1);

        // 执行测试
        orderService.shipOrder(orderId);

        // 验证
        verify(orderMapper).updateById(any(OrderOrder.class));
    }

    @Test
    @DisplayName("订单发货失败 - 订单状态不允许发货")
    void testShipOrderInvalidStatus() {
        // 准备测试数据 - 待付款订单
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 1, 0); // 待付款状态

        when(orderMapper.selectById(orderId)).thenReturn(order);

        // 执行测试并验证异常
        assertThatThrownBy(() -> orderService.shipOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("只有待发货订单才能发货");
    }

    @Test
    @DisplayName("获取用户订单列表成功")
    void testGetUserOrdersSuccess() {
        // 准备测试数据
        Long userId = 1L;
        OrderOrder order1 = createTestOrder(1L, "ORD001", userId, 2, 1);
        OrderOrder order2 = createTestOrder(2L, "ORD002", userId, 3, 1);

        IPage<OrderOrder> page = new Page<>(1, 10);
        ((Page<OrderOrder>) page).setRecords(Arrays.asList(order1, order2));
        ((Page<OrderOrder>) page).setTotal(2);

        when(orderMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(orderMapper.selectById(1L)).thenReturn(order1);
        when(orderMapper.selectById(2L)).thenReturn(order2);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        IPage<OrderVO> result = orderService.getUserOrders(new Page<>(1, 10), userId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(2);
    }

    @Test
    @DisplayName("获取用户订单列表 - 无订单")
    void testGetUserOrdersEmpty() {
        // 准备测试数据
        Long userId = 1L;
        IPage<OrderOrder> page = new Page<>(1, 10);
        ((Page<OrderOrder>) page).setRecords(Collections.emptyList());
        ((Page<OrderOrder>) page).setTotal(0);

        when(orderMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        IPage<OrderVO> result = orderService.getUserOrders(new Page<>(1, 10), userId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("订单状态描述正确")
    void testOrderStatusDescription() {
        // 验证各种状态的描述
        OrderOrder order1 = createTestOrder(1L, "ORD001", 1L, 1, 0);
        order1.setStatus(1);

        OrderOrder order2 = createTestOrder(2L, "ORD002", 1L, 2, 1);
        order2.setStatus(2);

        OrderOrder order3 = createTestOrder(3L, "ORD003", 1L, 3, 1);
        order3.setStatus(3);

        OrderOrder order4 = createTestOrder(4L, "ORD004", 1L, 4, 1);
        order4.setStatus(4);

        OrderOrder order5 = createTestOrder(5L, "ORD005", 1L, 5, 0);
        order5.setStatus(5);

        when(orderMapper.selectById(1L)).thenReturn(order1);
        when(orderMapper.selectById(2L)).thenReturn(order2);
        when(orderMapper.selectById(3L)).thenReturn(order3);
        when(orderMapper.selectById(4L)).thenReturn(order4);
        when(orderMapper.selectById(5L)).thenReturn(order5);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试并验证状态描述
        assertThat(orderService.getOrderDetail(1L).getStatusDesc()).isEqualTo("待付款");
        assertThat(orderService.getOrderDetail(2L).getStatusDesc()).isEqualTo("待发货");
        assertThat(orderService.getOrderDetail(3L).getStatusDesc()).isEqualTo("待收货");
        assertThat(orderService.getOrderDetail(4L).getStatusDesc()).isEqualTo("已完成");
        assertThat(orderService.getOrderDetail(5L).getStatusDesc()).isEqualTo("已取消");
    }

    @Test
    @DisplayName("支付状态描述正确")
    void testPayStatusDescription() {
        // 验证支付状态描述
        OrderOrder order1 = createTestOrder(1L, "ORD001", 1L, 1, 0); // 未支付
        OrderOrder order2 = createTestOrder(2L, "ORD002", 1L, 1, 1); // 已支付

        when(orderMapper.selectById(1L)).thenReturn(order1);
        when(orderMapper.selectById(2L)).thenReturn(order2);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试并验证支付状态描述
        assertThat(orderService.getOrderDetail(1L).getPayStatusDesc()).isEqualTo("未支付");
        assertThat(orderService.getOrderDetail(2L).getPayStatusDesc()).isEqualTo("已支付");
    }

    @Test
    @DisplayName("订单金额正确计算")
    void testOrderAmountCalculation() {
        // 准备测试数据 - 多商品订单
        Long orderId = 1L;
        OrderOrder order = createTestOrder(orderId, "ORD001", 1L, 1, 0);
        order.setTotalAmount(BigDecimal.valueOf(100.00));
        order.setPayAmount(BigDecimal.valueOf(100.00));

        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setOrderId(orderId);
        item1.setPrice(BigDecimal.valueOf(30.00));
        item1.setQuantity(2);
        item1.setTotalAmount(BigDecimal.valueOf(60.00));

        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setOrderId(orderId);
        item2.setPrice(BigDecimal.valueOf(40.00));
        item2.setQuantity(1);
        item2.setTotalAmount(BigDecimal.valueOf(40.00));

        when(orderMapper.selectById(orderId)).thenReturn(order);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(item1, item2));

        // 执行测试
        OrderVO result = orderService.getOrderDetail(orderId);

        // 验证订单金额
        assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(100.00));
        assertThat(result.getPayAmount()).isEqualTo(BigDecimal.valueOf(100.00));
    }
}
