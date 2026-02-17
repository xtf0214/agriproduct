package com.agriproduct.admin.service;

import com.agriproduct.admin.dto.UserStatusRequest;
import com.agriproduct.user.entity.SysUser;
import com.agriproduct.user.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 用户管理服务
 */
public interface UserManagementService {

    /**
     * 获取用户列表
     */
    IPage<UserInfoVO> getUserList(Page<SysUser> page);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param request 状态请求
     * @return 是否成功
     */
    Boolean updateUserStatus(Long userId, UserStatusRequest request);
}
