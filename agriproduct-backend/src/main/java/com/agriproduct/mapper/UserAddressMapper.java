package com.agriproduct.mapper;

import com.agriproduct.entity.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址Mapper
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
