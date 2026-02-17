package com.agriproduct.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.common.core.util.IdUtils;
import com.agriproduct.common.core.util.JwtUtils;
import com.agriproduct.user.dto.UserLoginRequest;
import com.agriproduct.user.dto.UserRegisterRequest;
import com.agriproduct.user.entity.SysUser;
import com.agriproduct.user.mapper.SysUserMapper;
import com.agriproduct.user.service.UserService;
import com.agriproduct.user.vo.UserLoginResponse;
import com.agriproduct.user.vo.UserInfoVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        // 查询用户
        SysUser user = getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成Token
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());

        return UserLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .userType(user.getUserType())
                .build();
    }

    @Override
    public Long register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        if (getByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已注册
        if (getByPhone(request.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setId(IdUtils.nextId());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setUserType(1); // 默认为普通用户
        user.setStatus(1);   // 默认正常状态

        save(user);
        return user.getId();
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }

    @Override
    public Boolean updateUserInfo(Long userId, SysUser user) {
        user.setId(userId);
        user.setPassword(null); // 不允许通过此接口修改密码
        user.setUsername(null); // 不允许修改用户名
        user.setUserType(null); // 不允许修改用户类型
        return updateById(user);
    }

    @Override
    public SysUser getByUsername(String username) {
        return lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();
    }

    @Override
    public SysUser getByPhone(String phone) {
        return lambdaQuery()
                .eq(SysUser::getPhone, phone)
                .one();
    }
}
