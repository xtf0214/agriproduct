package com.agriproduct.mapper;

import com.agriproduct.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品明细Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
