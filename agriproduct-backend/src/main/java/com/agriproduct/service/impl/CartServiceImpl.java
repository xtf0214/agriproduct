package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.agriproduct.dto.CartAddRequest;
import com.agriproduct.entity.CartShopping;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.CartShoppingMapper;
import com.agriproduct.mapper.ProdProductMapper;
import com.agriproduct.service.CartService;
import com.agriproduct.vo.CartVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartShoppingMapper, CartShopping> implements CartService {

    private final ProdProductMapper productMapper;

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
        ProdProduct product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        CartShopping existCart = getOne(
                new LambdaQueryWrapper<CartShopping>()
                        .eq(CartShopping::getUserId, userId)
                        .eq(CartShopping::getProductId, request.getProductId())
        );

        if (existCart != null) {
            int newQuantity = existCart.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new BusinessException("库存不足");
            }
            existCart.setQuantity(newQuantity);
            updateById(existCart);
        } else {
            CartShopping cart = new CartShopping();
            cart.setUserId(userId);
            cart.setProductId(request.getProductId());
            cart.setQuantity(request.getQuantity());
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
        
        ProdProduct product = productMapper.selectById(cart.getProductId());
        if (product != null && product.getStock() < quantity) {
            throw new BusinessException("库存不足");
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
        remove(new LambdaQueryWrapper<CartShopping>().eq(CartShopping::getUserId, userId));
    }

    @Override
    public Integer getCartCount(Long userId) {
        List<CartShopping> cartList = list(
                new LambdaQueryWrapper<CartShopping>().eq(CartShopping::getUserId, userId)
        );
        return cartList.stream()
                .map(CartShopping::getQuantity)
                .reduce(0, Integer::sum);
    }

    private CartVO convertToVO(CartShopping cart) {
        CartVO vo = BeanUtil.copyProperties(cart, CartVO.class);
        
        // 从商品表获取最新信息
        ProdProduct product = productMapper.selectById(cart.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            vo.setProductImage(product.getMainImage());
            vo.setProductPrice(product.getPrice());
            if (product.getPrice() != null && cart.getQuantity() != null) {
                vo.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }
        }
        
        vo.setSelected(false);
        return vo;
    }
}
