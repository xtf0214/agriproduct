package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.OrderCreateRequest;
import com.agriproduct.entity.OrderOrder;
import com.agriproduct.service.OrderService;
import com.agriproduct.vo.OrderVO;
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

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> createOrder(@RequestHeader("X-User-Id") Long userId,
                                        @Valid @RequestBody OrderCreateRequest request) {
        OrderVO order = orderService.createOrder(userId, request);
        return Result.success(order);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO order = orderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    @Operation(summary = "获取用户订单列表")
    @GetMapping("/list")
    public Result<IPage<OrderVO>> getUserOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OrderOrder> page = new Page<>(pageNum, pageSize);
        IPage<OrderVO> result = orderService.getUserOrders(page, userId, status);
        return Result.success(result);
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId,
                                     @RequestHeader("X-User-Id") Long userId) {
        orderService.cancelOrder(orderId, userId);
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PutMapping("/{orderId}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long orderId,
                                      @RequestHeader("X-User-Id") Long userId) {
        orderService.confirmOrder(orderId, userId);
        return Result.success();
    }

    @Operation(summary = "支付订单(模拟)")
    @PutMapping("/{orderId}/pay")
    public Result<Void> payOrder(@PathVariable Long orderId,
                                  @RequestHeader("X-User-Id") Long userId) {
        orderService.payOrder(orderId, userId);
        return Result.success();
    }
}
