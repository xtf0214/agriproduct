package com.agriproduct.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.order.dto.CartAddRequest;
import com.agriproduct.order.entity.CartShopping;
import com.agriproduct.order.mapper.CartShoppingMapper;
import com.agriproduct.order.service.CartService;
import com.agriproduct.order.vo.CartVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartShoppingMapper, CartShopping> implements CartService {

    @Override
    public List<CartVO> getUserCart(Long userId) {
        List<CartShopping> cartList = list(
                new LambdaQueryWrapper<CartShopping>()
                        .eq(CartShopping::getUserId, userId)
                        .orderByDesc(CartShopping::getCreateTime)
        );

        return cartList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void addToCart(Long userId, CartAddRequest request) {
        // 查询是否已存在
        CartShopping existCart = getOne(
                new LambdaQueryWrapper<CartShopping>()
                        .eq(CartShopping::getUserId, userId)
                        .eq(CartShopping::getProductId, request.getProductId())
        );

        if (existCart != null) {
            // 已存在，更新数量
            existCart.setQuantity(existCart.getQuantity() + request.getQuantity());
            updateById(existCart);
        } else {
            // 不存在，新增
            // TODO: 从商品服务获取商品信息
            CartShopping cart = new CartShopping();
            cart.setUserId(userId);
            cart.setProductId(request.getProductId());
            cart.setQuantity(request.getQuantity());
            // 暂时使用默认值，实际应从商品服务获取
            cart.setProductName("商品名称");
            cart.setProductImage("");
            cart.setProductPrice(BigDecimal.ZERO);
            save(cart);
        }
    }

    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        CartShopping cart = getById(cartId);
        if (cart == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (quantity <= 0) {
            throw new BusinessException("数量必须大于0");
        }
        cart.setQuantity(quantity);
        updateById(cart);
    }

    @Override
    public void removeCartItem(Long cartId) {
        removeById(cartId);
    }

    @Override
    public void clearCart(Long userId) {
        remove(
                new LambdaQueryWrapper<CartShopping>()
                        .eq(CartShopping::getUserId, userId)
        );
    }

    @Override
    public Integer getCartCount(Long userId) {
        List<CartShopping> cartList = list(
                new LambdaQueryWrapper<CartShopping>()
                        .eq(CartShopping::getUserId, userId)
        );
        return cartList.stream()
                .map(CartShopping::getQuantity)
                .reduce(0, Integer::sum);
    }

    /**
     * 转换为VO
     */
    private CartVO convertToVO(CartShopping cart) {
        CartVO vo = BeanUtil.copyProperties(cart, CartVO.class);
        // 计算小计
        vo.setSubtotal(cart.getProductPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        vo.setSelected(false); // 默认不选中
        return vo;
    }
}
