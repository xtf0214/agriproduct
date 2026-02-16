package com.agriproduct.user.controller;

import com.agriproduct.common.core.domain.Result;
import com.agriproduct.user.dto.UserLoginRequest;
import com.agriproduct.user.dto.UserRegisterRequest;
import com.agriproduct.user.entity.SysUser;
import com.agriproduct.user.service.UserService;
import com.agriproduct.user.vo.UserLoginResponse;
import com.agriproduct.user.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return Result.success(response);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody UserRegisterRequest request) {
        Long userId = userService.register(request);
        return Result.success(userId);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/profile")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息")
    @PutMapping("/profile")
    public Result<Void> updateUserInfo(@RequestHeader("X-User-Id") Long userId,
                                        @RequestBody SysUser user) {
        userService.updateUserInfo(userId, user);
        return Result.success();
    }
}
