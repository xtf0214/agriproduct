package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.agriproduct.dto.LoginRequest;
import com.agriproduct.dto.RegisterRequest;
import com.agriproduct.dto.UserUpdateRequest;
import com.agriproduct.entity.Merchant;
import com.agriproduct.entity.SysUser;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.MerchantMapper;
import com.agriproduct.mapper.SysUserMapper;
import com.agriproduct.service.AuthService;
import com.agriproduct.util.IdUtils;
import com.agriproduct.util.JwtUtils;
import com.agriproduct.vo.LoginResponse;
import com.agriproduct.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 统一认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final MerchantMapper merchantMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
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

        // 根据登录类型验证权限
        String loginType = request.getLoginType();
        Integer userType = user.getUserType();

        if (loginType != null) {
            switch (loginType.toLowerCase()) {
                case "merchant":
                    if (userType != 2) {
                        throw new BusinessException("该账号不是商家账号");
                    }
                    break;
                case "admin":
                    if (userType != 3) {
                        throw new BusinessException("该账号不是管理员账号");
                    }
                    break;
                case "user":
                    // 普通用户登录，不限制
                    break;
                default:
                    // 自动判断，不做限制
                    break;
            }
        }

        // 生成Token
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());

        // 构建响应
        LoginResponse.LoginResponseBuilder builder = LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .userType(userType)
                .userTypeDesc(LoginResponse.getUserTypeDesc(userType));

        // 如果是商家，返回商家信息
        if (userType == 2) {
            LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Merchant::getUserId, user.getId());
            Merchant merchant = merchantMapper.selectOne(wrapper);

            if (merchant != null) {
                builder.merchantId(merchant.getId())
                       .shopName(merchant.getShopName())
                       .merchantStatus(merchant.getStatus());

                // 商家未通过审核
                if (merchant.getStatus() == 0) {
                    throw new BusinessException("您的商家申请正在审核中");
                }
                if (merchant.getStatus() == 2) {
                    throw new BusinessException("您的商家申请已被拒绝");
                }
            }
        }

        return builder.build();
    }

    @Override
    public Long register(RegisterRequest request) {
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
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());

        // 根据注册类型设置用户类型
        String registerType = request.getRegisterType();
        if ("merchant".equalsIgnoreCase(registerType)) {
            user.setUserType(2); // 商家
        } else {
            user.setUserType(1); // 默认为普通用户
        }

        user.setStatus(1); // 默认正常状态

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
    public Boolean updateUserInfo(Long userId, UserUpdateRequest request) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新昵称
        if (StrUtil.isNotBlank(request.getNickname())) {
            user.setNickname(request.getNickname());
        }

        // 更新头像
        if (StrUtil.isNotBlank(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }

        // 更新手机号
        if (StrUtil.isNotBlank(request.getPhone())) {
            // 检查手机号是否被其他用户使用
            SysUser existingUser = getByPhone(request.getPhone());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException("该手机号已被其他用户使用");
            }
            user.setPhone(request.getPhone());
        }

        return updateById(user);
    }

    /**
     * 根据用户名查询用户
     */
    private SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(wrapper);
    }

    /**
     * 根据手机号查询用户
     */
    private SysUser getByPhone(String phone) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        return sysUserMapper.selectOne(wrapper);
    }
}
