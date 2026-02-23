package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.LoginRequest;
import com.agriproduct.dto.RegisterRequest;
import com.agriproduct.service.AuthService;
import com.agriproduct.vo.LoginResponse;
import com.agriproduct.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 统一认证控制器
 * 支持用户、商家、管理员的登录注册
 */
@Tag(name = "统一认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 统一登录
     */
    @Operation(summary = "统一登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    /**
     * 统一注册
     */
    @Operation(summary = "统一注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody RegisterRequest request) {
        Long userId = authService.register(request);
        return Result.success(userId);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/profile")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        UserInfoVO userInfo = authService.getUserInfo(userId);
        return Result.success(userInfo);
    }
}
