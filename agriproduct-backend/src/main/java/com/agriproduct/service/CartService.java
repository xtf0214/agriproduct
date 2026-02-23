package com.agriproduct.service;

import com.agriproduct.dto.CartAddRequest;
import com.agriproduct.entity.CartShopping;
import com.agriproduct.vo.CartVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<CartShopping> {

    List<CartVO> getUserCart(Long userId);

    void addToCart(Long userId, CartAddRequest request);

    void updateQuantity(Long cartId, Integer quantity);

    void removeCartItem(Long cartId);

    void clearCart(Long userId);

    Integer getCartCount(Long userId);
}
