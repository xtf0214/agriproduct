package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.CartAddRequest;
import com.agriproduct.service.CartService;
import com.agriproduct.vo.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@Tag(name = "购物车管理")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "获取购物车列表")
    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(@RequestHeader("X-User-Id") Long userId) {
        List<CartVO> cartList = cartService.getUserCart(userId);
        return Result.success(cartList);
    }

    @Operation(summary = "添加商品到购物车")
    @PostMapping
    public Result<Void> addToCart(@RequestHeader("X-User-Id") Long userId,
                                   @RequestBody CartAddRequest request) {
        cartService.addToCart(userId, request);
        return Result.success();
    }

    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/{cartId}")
    public Result<Void> updateQuantity(@PathVariable Long cartId,
                                        @RequestParam Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.success();
    }

    @Operation(summary = "删除购物车项")
    @DeleteMapping("/{cartId}")
    public Result<Void> removeCartItem(@PathVariable Long cartId) {
        cartService.removeCartItem(cartId);
        return Result.success();
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public Result<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return Result.success();
    }

    @Operation(summary = "获取购物车商品数量")
    @GetMapping("/count")
    public Result<Integer> getCartCount(@RequestHeader("X-User-Id") Long userId) {
        Integer count = cartService.getCartCount(userId);
        return Result.success(count);
    }
}
