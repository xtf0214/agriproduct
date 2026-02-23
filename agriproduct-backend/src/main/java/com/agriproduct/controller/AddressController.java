package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.AddressRequest;
import com.agriproduct.service.AddressService;
import com.agriproduct.vo.AddressVO;
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
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "获取地址列表")
    @GetMapping("/list")
    public Result<List<AddressVO>> getAddressList(@RequestHeader("X-User-Id") Long userId) {
        List<AddressVO> list = addressService.getAddressList(userId);
        return Result.success(list);
    }

    @Operation(summary = "获取地址详情")
    @GetMapping("/{addressId}")
    public Result<AddressVO> getAddress(@PathVariable Long addressId,
                                         @RequestHeader("X-User-Id") Long userId) {
        AddressVO address = addressService.getAddress(addressId, userId);
        return Result.success(address);
    }

    @Operation(summary = "创建收货地址")
    @PostMapping
    public Result<Long> createAddress(@RequestHeader("X-User-Id") Long userId,
                                       @Valid @RequestBody AddressRequest request) {
        Long addressId = addressService.createAddress(userId, request);
        return Result.success(addressId);
    }

    @Operation(summary = "修改收货地址")
    @PutMapping("/{addressId}")
    public Result<Boolean> updateAddress(@PathVariable Long addressId,
                                          @RequestHeader("X-User-Id") Long userId,
                                          @Valid @RequestBody AddressRequest request) {
        Boolean result = addressService.updateAddress(addressId, userId, request);
        return Result.success(result);
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/{addressId}")
    public Result<Boolean> deleteAddress(@PathVariable Long addressId,
                                          @RequestHeader("X-User-Id") Long userId) {
        Boolean result = addressService.deleteAddress(addressId, userId);
        return Result.success(result);
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/{addressId}/default")
    public Result<Boolean> setDefaultAddress(@PathVariable Long addressId,
                                              @RequestHeader("X-User-Id") Long userId) {
        Boolean result = addressService.setDefaultAddress(addressId, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public Result<AddressVO> getDefaultAddress(@RequestHeader("X-User-Id") Long userId) {
        AddressVO address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
}
