package com.agriproduct.order.controller;

import com.agriproduct.common.core.domain.Result;
import com.agriproduct.order.dto.OrderCreateRequest;
import com.agriproduct.order.service.OrderService;
import com.agriproduct.order.vo.OrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> createOrder(@RequestHeader("X-User-Id") Long userId,
                                       @Valid @RequestBody OrderCreateRequest request) {
        OrderVO order = orderService.createOrder(userId, request);
        return Result.success(order);
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO order = orderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    /**
     * 获取用户订单列表
     */
    @Operation(summary = "获取用户订单列表")
    @GetMapping("/list")
    public Result<IPage<OrderVO>> getUserOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OrderVO> page = new Page<>(pageNum, pageSize);
        IPage<OrderVO> orderPage = orderService.getUserOrders(new Page<>(pageNum, pageSize), userId);
        return Result.success(orderPage);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return Result.success();
    }

    /**
     * 确认收货
     */
    @Operation(summary = "确认收货")
    @PutMapping("/{orderId}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return Result.success();
    }

    /**
     * 订单发货（商家端）
     */
    @Operation(summary = "订单发货")
    @PutMapping("/{orderId}/ship")
    public Result<Void> shipOrder(@PathVariable Long orderId) {
        orderService.shipOrder(orderId);
        return Result.success();
    }
}
