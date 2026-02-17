package com.agriproduct.user.service;

import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.user.dto.UserLoginRequest;
import com.agriproduct.user.dto.UserRegisterRequest;
import com.agriproduct.user.entity.SysUser;
import com.agriproduct.user.mapper.SysUserMapper;
import com.agriproduct.user.service.impl.UserServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private SysUserMapper userMapper;

    private UserServiceImpl userService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl();
        // 使用反射注入 baseMapper
        Field baseMapperField = userService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(userService, userMapper);
    }

    @Test
    @DisplayName("登录成功 - 用户名密码正确")
    void testLoginSuccess() {
        // 准备测试数据
        String username = "testuser";
        String password = "123456";
        String encodedPassword = passwordEncoder.encode(password);

        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setNickname("测试用户");
        user.setStatus(1);
        user.setUserType(1);

        // 模拟mapper返回
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        // 执行测试
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        var result = userService.login(request);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getToken()).isNotEmpty();
    }

    @Test
    @DisplayName("登录失败 - 用户名不存在")
    void testLoginUserNotFound() {
        // 模拟用户不存在
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("notexist");
        request.setPassword("123456");

        // 验证异常
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名或密码错误");
    }

    @Test
    @DisplayName("登录失败 - 用户已被禁用")
    void testLoginUserDisabled() {
        // 准备测试数据 - 禁用状态用户
        String password = "123456";
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(0); // 禁用状态

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        // 执行测试
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("testuser");
        request.setPassword(password);

        // 验证异常
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("账号已被禁用");
    }

    @Test
    @DisplayName("注册成功 - 新用户注册")
    void testRegisterSuccess() {
        // 准备测试数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setNickname("新用户");
        request.setPhone("13900139000");

        // 模拟用户名和手机号不存在
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userMapper.insert(any(SysUser.class))).thenReturn(1);

        // 执行测试
        Long userId = userService.register(request);

        // 验证
        assertThat(userId).isNotNull();
        verify(userMapper).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void testRegisterUsernameExists() {
        // 准备测试数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("existuser");
        request.setPassword("123456");
        request.setNickname("测试用户");
        request.setPhone("13900139000");

        // 模拟用户名已存在
        SysUser existUser = new SysUser();
        existUser.setId(1L);
        existUser.setUsername("existuser");
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existUser);

        // 验证异常
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名已存在");
    }

    @Test
    @DisplayName("注册失败 - 手机号已注册")
    void testRegisterPhoneExists() {
        // 准备测试数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setNickname("新用户");
        request.setPhone("13900139000");

        // 模拟：用户名不存在，但手机号已存在
        when(userMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null)  // 第一次查询用户名
                .thenReturn(new SysUser());  // 第二次查询手机号返回用户

        // 验证异常
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("手机号已被注册");
    }

    @Test
    @DisplayName("获取用户信息成功")
    void testGetUserInfoSuccess() {
        // 准备测试数据
        Long userId = 1L;
        SysUser user = new SysUser();
        user.setId(userId);
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setPhone("13900139000");
        user.setStatus(1);
        user.setUserType(1);

        when(userMapper.selectById(userId)).thenReturn(user);

        // 执行测试
        var result = userService.getUserInfo(userId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("获取用户信息失败 - 用户不存在")
    void testGetUserInfoNotFound() {
        // 模拟用户不存在
        when(userMapper.selectById(999L)).thenReturn(null);

        // 验证异常
        assertThatThrownBy(() -> userService.getUserInfo(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户不存在");
    }
}
