package com.agriproduct.order.service;

import com.agriproduct.order.dto.CartAddRequest;
import com.agriproduct.order.entity.CartShopping;
import com.agriproduct.order.vo.CartVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<CartShopping> {

    /**
     * 获取用户购物车列表
     */
    List<CartVO> getUserCart(Long userId);

    /**
     * 添加商品到购物车
     */
    void addToCart(Long userId, CartAddRequest request);

    /**
     * 更新购物车商品数量
     */
    void updateQuantity(Long cartId, Integer quantity);

    /**
     * 删除购物车项
     */
    void removeCartItem(Long cartId);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);

    /**
     * 获取购物车商品数量
     */
    Integer getCartCount(Long userId);
}
