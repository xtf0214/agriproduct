package com.agriproduct.service;

import com.agriproduct.dto.LoginRequest;
import com.agriproduct.dto.RegisterRequest;
import com.agriproduct.dto.UserUpdateRequest;
import com.agriproduct.vo.LoginResponse;
import com.agriproduct.vo.UserInfoVO;

/**
 * 统一认证服务接口
 */
public interface AuthService {

    /**
     * 统一登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 统一注册
     *
     * @param request 注册请求
     * @return 用户ID
     */
    Long register(RegisterRequest request);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateUserInfo(Long userId, UserUpdateRequest request);
}
