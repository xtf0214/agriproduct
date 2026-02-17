package com.agriproduct.order.mapper;

import com.agriproduct.order.entity.CartShopping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车Mapper
 */
@Mapper
public interface CartShoppingMapper extends BaseMapper<CartShopping> {
}
