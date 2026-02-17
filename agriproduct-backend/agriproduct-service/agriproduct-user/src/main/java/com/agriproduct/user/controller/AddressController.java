package com.agriproduct.user.controller;

import com.agriproduct.common.core.domain.Result;
import com.agriproduct.user.dto.AddressCreateRequest;
import com.agriproduct.user.service.AddressService;
import com.agriproduct.user.vo.AddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@Tag(name = "收货地址管理")
@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * 获取地址列表
     */
    @Operation(summary = "获取地址列表")
    @GetMapping
    public Result<List<AddressVO>> getAddressList(@RequestHeader("X-User-Id") Long userId) {
        List<AddressVO> list = addressService.getAddressList(userId);
        return Result.success(list);
    }

    /**
     * 获取地址详情
     */
    @Operation(summary = "获取地址详情")
    @GetMapping("/{addressId}")
    public Result<AddressVO> getAddress(@RequestHeader("X-User-Id") Long userId,
                                         @PathVariable Long addressId) {
        AddressVO address = addressService.getAddress(addressId, userId);
        return Result.success(address);
    }

    /**
     * 创建收货地址
     */
    @Operation(summary = "创建收货地址")
    @PostMapping
    public Result<Long> createAddress(@RequestHeader("X-User-Id") Long userId,
                                       @Valid @RequestBody AddressCreateRequest request) {
        Long addressId = addressService.createAddress(userId, request);
        return Result.success(addressId);
    }

    /**
     * 修改收货地址
     */
    @Operation(summary = "修改收货地址")
    @PutMapping("/{addressId}")
    public Result<Void> updateAddress(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable Long addressId,
                                       @Valid @RequestBody AddressCreateRequest request) {
        addressService.updateAddress(addressId, userId, request);
        return Result.success();
    }

    /**
     * 删除收货地址
     */
    @Operation(summary = "删除收货地址")
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable Long addressId) {
        addressService.deleteAddress(addressId, userId);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @Operation(summary = "设置默认地址")
    @PutMapping("/{addressId}/default")
    public Result<Void> setDefaultAddress(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable Long addressId) {
        addressService.setDefaultAddress(addressId, userId);
        return Result.success();
    }

    /**
     * 获取默认地址
     */
    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public Result<AddressVO> getDefaultAddress(@RequestHeader("X-User-Id") Long userId) {
        AddressVO address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
}
