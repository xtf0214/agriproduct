package com.agriproduct.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.common.core.util.IdUtils;
import com.agriproduct.user.dto.AddressCreateRequest;
import com.agriproduct.user.entity.UserAddress;
import com.agriproduct.user.mapper.UserAddressMapper;
import com.agriproduct.user.service.AddressService;
import com.agriproduct.user.vo.AddressVO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收货地址服务实现
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements AddressService {

    @Override
    public List<AddressVO> getAddressList(Long userId) {
        return lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime)
                .list()
                .stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressVO getAddress(Long addressId, Long userId) {
        UserAddress address = lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .one();
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return convertToVO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAddress(Long userId, AddressCreateRequest request) {
        UserAddress address = new UserAddress();
        address.setId(IdUtils.nextId());
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);

        // 如果设置为默认地址，取消其他默认地址
        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }

        save(address);
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAddress(Long addressId, Long userId, AddressCreateRequest request) {
        // 验证地址是否属于该用户
        UserAddress existingAddress = lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .one();
        if (existingAddress == null) {
            throw new BusinessException("地址不存在");
        }

        // 如果设置为默认地址，取消其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }

        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }

        return updateById(address);
    }

    @Override
    public Boolean deleteAddress(Long addressId, Long userId) {
        // 验证地址是否属于该用户
        UserAddress address = lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .one();
        if (address == null) {
            throw new BusinessException("地址不存在");
        }

        return removeById(addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefaultAddress(Long addressId, Long userId) {
        // 验证地址是否属于该用户
        UserAddress address = lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .one();
        if (address == null) {
            throw new BusinessException("地址不存在");
        }

        // 取消其他默认地址
        cancelDefaultAddress(userId);

        // 设置为默认地址
        address.setIsDefault(1);
        return updateById(address);
    }

    @Override
    public AddressVO getDefaultAddress(Long userId) {
        UserAddress address = lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
                .one();
        return address != null ? convertToVO(address) : null;
    }

    /**
     * 取消用户的所有默认地址
     */
    private void cancelDefaultAddress(Long userId) {
        update(new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0));
    }

    /**
     * 转换为VO
     */
    private AddressVO convertToVO(UserAddress address) {
        AddressVO vo = BeanUtil.copyProperties(address, AddressVO.class);
        // 拼接完整地址
        String fullAddress = "";
        if (StrUtil.isNotBlank(address.getProvince())) {
            fullAddress += address.getProvince();
        }
        if (StrUtil.isNotBlank(address.getCity())) {
            fullAddress += address.getCity();
        }
        if (StrUtil.isNotBlank(address.getDistrict())) {
            fullAddress += address.getDistrict();
        }
        if (StrUtil.isNotBlank(address.getDetailAddress())) {
            fullAddress += address.getDetailAddress();
        }
        vo.setFullAddress(fullAddress);
        return vo;
    }
}
