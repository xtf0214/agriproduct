package com.agriproduct.user.service;

import com.agriproduct.user.dto.AddressCreateRequest;
import com.agriproduct.user.entity.UserAddress;
import com.agriproduct.user.vo.AddressVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService extends IService<UserAddress> {

    /**
     * 获取用户地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<AddressVO> getAddressList(Long userId);

    /**
     * 获取地址详情
     *
     * @param addressId 地址ID
     * @param userId    用户ID
     * @return 地址详情
     */
    AddressVO getAddress(Long addressId, Long userId);

    /**
     * 创建收货地址
     *
     * @param userId  用户ID
     * @param request 地址请求
     * @return 地址ID
     */
    Long createAddress(Long userId, AddressCreateRequest request);

    /**
     * 修改收货地址
     *
     * @param addressId 地址ID
     * @param userId    用户ID
     * @param request   地址请求
     * @return 是否成功
     */
    Boolean updateAddress(Long addressId, Long userId, AddressCreateRequest request);

    /**
     * 删除收货地址
     *
     * @param addressId 地址ID
     * @param userId    用户ID
     * @return 是否成功
     */
    Boolean deleteAddress(Long addressId, Long userId);

    /**
     * 设置默认地址
     *
     * @param addressId 地址ID
     * @param userId    用户ID
     * @return 是否成功
     */
    Boolean setDefaultAddress(Long addressId, Long userId);

    /**
     * 获取用户默认地址
     *
     * @param userId 用户ID
     * @return 默认地址
     */
    AddressVO getDefaultAddress(Long userId);
}
