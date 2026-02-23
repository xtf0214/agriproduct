package com.agriproduct.service;

import com.agriproduct.dto.AddressRequest;
import com.agriproduct.entity.UserAddress;
import com.agriproduct.vo.AddressVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService extends IService<UserAddress> {

    List<AddressVO> getAddressList(Long userId);

    AddressVO getAddress(Long addressId, Long userId);

    Long createAddress(Long userId, AddressRequest request);

    Boolean updateAddress(Long addressId, Long userId, AddressRequest request);

    Boolean deleteAddress(Long addressId, Long userId);

    Boolean setDefaultAddress(Long addressId, Long userId);

    AddressVO getDefaultAddress(Long userId);
}
