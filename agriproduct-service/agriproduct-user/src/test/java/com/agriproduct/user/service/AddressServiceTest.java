package com.agriproduct.user.service;

import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.user.dto.AddressCreateRequest;
import com.agriproduct.user.entity.UserAddress;
import com.agriproduct.user.mapper.UserAddressMapper;
import com.agriproduct.user.service.impl.AddressServiceImpl;
import com.agriproduct.user.vo.AddressVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 地址服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("地址服务测试")
class AddressServiceTest {

    @Mock
    private UserAddressMapper addressMapper;

    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() throws Exception {
        addressService = new AddressServiceImpl();
        // 使用反射注入 baseMapper
        Field baseMapperField = addressService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(addressService, addressMapper);
    }

    @Test
    @DisplayName("获取用户地址列表成功")
    void testGetAddressListSuccess() {
        // 准备测试数据
        Long userId = 1L;
        UserAddress address1 = new UserAddress();
        address1.setId(1L);
        address1.setUserId(userId);
        address1.setReceiverName("张三");
        address1.setReceiverPhone("13900139000");
        address1.setProvince("北京市");
        address1.setCity("北京市");
        address1.setDistrict("朝阳区");
        address1.setDetailAddress("xx街道xx号");
        address1.setIsDefault(1);

        when(addressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(address1));

        // 执行测试
        List<AddressVO> result = addressService.getAddressList(userId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReceiverName()).isEqualTo("张三");
    }

    @Test
    @DisplayName("获取地址列表 - 无地址")
    void testGetAddressListEmpty() {
        // 模拟返回空列表
        when(addressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<AddressVO> result = addressService.getAddressList(1L);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("获取单个地址成功")
    void testGetAddressSuccess() {
        // 准备测试数据
        Long userId = 1L;
        Long addressId = 1L;

        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        address.setReceiverName("张三");
        address.setReceiverPhone("13900139000");
        address.setProvince("北京市");
        address.setCity("北京市");

        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(address);

        // 执行测试
        AddressVO result = addressService.getAddress(addressId, userId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getReceiverName()).isEqualTo("张三");
    }

    @Test
    @DisplayName("获取地址失败 - 地址不存在")
    void testGetAddressNotFound() {
        // 模拟地址不存在
        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        assertThatThrownBy(() -> addressService.getAddress(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("地址不存在");
    }

    @Test
    @DisplayName("创建地址成功")
    void testCreateAddressSuccess() {
        // 准备测试数据
        Long userId = 1L;
        AddressCreateRequest request = new AddressCreateRequest();
        request.setReceiverName("张三");
        request.setReceiverPhone("13900139000");
        request.setProvince("北京市");
        request.setCity("北京市");
        request.setDistrict("朝阳区");
        request.setDetailAddress("xx街道xx号");
        request.setIsDefault(0);

        when(addressMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        when(addressMapper.insert(any(UserAddress.class))).thenReturn(1);

        // 执行测试
        Long addressId = addressService.createAddress(userId, request);

        // 验证结果
        assertThat(addressId).isNotNull();
    }

    @Test
    @Disabled("需要MyBatis-Plus TableInfo缓存，适合集成测试")
    @DisplayName("创建默认地址 - 取消其他默认地址")
    void testCreateDefaultAddress() {
        // 准备测试数据
        Long userId = 1L;
        AddressCreateRequest request = new AddressCreateRequest();
        request.setReceiverName("张三");
        request.setReceiverPhone("13900139000");
        request.setIsDefault(1);

        when(addressMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        when(addressMapper.insert(any(UserAddress.class))).thenReturn(1);

        // 执行测试
        addressService.createAddress(userId, request);

        // 验证：旧的默认地址应该被取消
        verify(addressMapper, atLeastOnce()).update(any(), any(LambdaUpdateWrapper.class));
    }

    @Test
    @DisplayName("更新地址成功")
    void testUpdateAddressSuccess() {
        // 准备测试数据
        Long userId = 1L;
        Long addressId = 1L;
        AddressCreateRequest request = new AddressCreateRequest();
        request.setReceiverName("张三改");
        request.setReceiverPhone("13900139000");
        request.setProvince("北京市");
        request.setCity("北京市");
        request.setDistrict("海淀区");
        request.setDetailAddress("新的地址");
        request.setIsDefault(0);

        UserAddress existAddress = new UserAddress();
        existAddress.setId(addressId);
        existAddress.setUserId(userId);

        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existAddress);
        when(addressMapper.updateById(any(UserAddress.class))).thenReturn(1);

        // 执行测试
        Boolean result = addressService.updateAddress(addressId, userId, request);

        // 验证
        assertThat(result).isTrue();
        verify(addressMapper).updateById(any(UserAddress.class));
    }

    @Test
    @DisplayName("更新地址失败 - 地址不存在")
    void testUpdateAddressNotFound() {
        // 准备测试数据
        Long userId = 1L;
        Long addressId = 1L;
        AddressCreateRequest request = new AddressCreateRequest();
        request.setReceiverName("张三");

        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        assertThatThrownBy(() -> addressService.updateAddress(addressId, userId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("地址不存在");
    }

    @Test
    @Disabled("需要MyBatis-Plus TableInfo缓存，适合集成测试")
    @DisplayName("删除地址成功")
    void testDeleteAddressSuccess() {
        // 准备测试数据
        Long userId = 1L;
        Long addressId = 1L;

        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        address.setIsDefault(0);

        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(address);
        when(addressMapper.deleteById(addressId)).thenReturn(1);

        // 执行测试
        Boolean result = addressService.deleteAddress(addressId, userId);

        // 验证
        assertThat(result).isTrue();
        verify(addressMapper).deleteById(addressId);
    }

    @Test
    @Disabled("需要MyBatis-Plus TableInfo缓存，适合集成测试")
    @DisplayName("设置默认地址成功")
    void testSetDefaultAddressSuccess() {
        // 准备测试数据
        Long userId = 1L;
        Long addressId = 1L;

        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        address.setIsDefault(0);

        when(addressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(address)
                .thenReturn(null); // 第二次查询取消默认地址时返回null
        when(addressMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        when(addressMapper.updateById(any(UserAddress.class))).thenReturn(1);

        // 执行测试
        Boolean result = addressService.setDefaultAddress(addressId, userId);

        // 验证
        assertThat(result).isTrue();
        verify(addressMapper, atLeastOnce()).update(any(), any(LambdaUpdateWrapper.class));
    }

    @Test
    @DisplayName("设置默认地址失败 - 地址不存在")
    void testSetDefaultAddressNotFound() {
        // 准备测试数据
        Long userId = 1L;
        Long addressId = 1L;

        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        assertThatThrownBy(() -> addressService.setDefaultAddress(addressId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("地址不存在");
    }

    @Test
    @DisplayName("获取默认地址成功")
    void testGetDefaultAddressSuccess() {
        // 准备测试数据
        Long userId = 1L;

        UserAddress address = new UserAddress();
        address.setId(1L);
        address.setUserId(userId);
        address.setReceiverName("张三");
        address.setIsDefault(1);

        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(address);

        // 执行测试
        AddressVO result = addressService.getDefaultAddress(userId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getReceiverName()).isEqualTo("张三");
        assertThat(result.getIsDefault()).isEqualTo(1);
    }

    @Test
    @DisplayName("获取默认地址 - 无默认地址")
    void testGetDefaultAddressNotExist() {
        // 模拟无默认地址
        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试
        AddressVO result = addressService.getDefaultAddress(1L);

        // 验证结果
        assertThat(result).isNull();
    }
}
