package com.agriproduct.user.service;

import com.agriproduct.user.dto.UserLoginRequest;
import com.agriproduct.user.dto.UserRegisterRequest;
import com.agriproduct.user.entity.SysUser;
import com.agriproduct.user.vo.UserLoginResponse;
import com.agriproduct.user.vo.UserInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface UserService extends IService<SysUser> {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    UserLoginResponse login(UserLoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户ID
     */
    Long register(UserRegisterRequest request);

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
     * @param user   用户信息
     * @return 是否成功
     */
    Boolean updateUserInfo(Long userId, SysUser user);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser getByPhone(String phone);
}
