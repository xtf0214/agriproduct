package com.agriproduct.user.mapper;

import com.agriproduct.user.entity.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址Mapper
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
