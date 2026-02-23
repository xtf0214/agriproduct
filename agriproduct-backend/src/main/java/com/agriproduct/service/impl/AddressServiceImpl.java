package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.agriproduct.dto.AddressRequest;
import com.agriproduct.entity.UserAddress;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.UserAddressMapper;
import com.agriproduct.service.AddressService;
import com.agriproduct.util.IdUtils;
import com.agriproduct.vo.AddressVO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public Long createAddress(Long userId, AddressRequest request) {
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

        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }

        save(address);
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAddress(Long addressId, Long userId, AddressRequest request) {
        UserAddress existingAddress = lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .one();
        if (existingAddress == null) {
            throw new BusinessException("地址不存在");
        }

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
        UserAddress address = lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .one();
        if (address == null) {
            throw new BusinessException("地址不存在");
        }

        cancelDefaultAddress(userId);

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

    private void cancelDefaultAddress(Long userId) {
        update(new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0));
    }

    private AddressVO convertToVO(UserAddress address) {
        AddressVO vo = BeanUtil.copyProperties(address, AddressVO.class);
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
