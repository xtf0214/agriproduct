package com.agriproduct.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.admin.dto.UserStatusRequest;
import com.agriproduct.admin.service.UserManagementService;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.user.entity.SysUser;
import com.agriproduct.user.mapper.SysUserMapper;
import com.agriproduct.user.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户管理服务实现
 */
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final SysUserMapper userMapper;

    @Override
    public IPage<UserInfoVO> getUserList(Page<SysUser> page) {
        IPage<SysUser> userPage = userMapper.selectPage(page, null);

        // 转换为VO
        IPage<UserInfoVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList()));

        return voPage;
    }

    @Override
    public Boolean updateUserStatus(Long userId, UserStatusRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(request.getStatus());
        return userMapper.updateById(user) > 0;
    }

    /**
     * 转换为VO
     */
    private UserInfoVO convertToVO(SysUser user) {
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }
}
