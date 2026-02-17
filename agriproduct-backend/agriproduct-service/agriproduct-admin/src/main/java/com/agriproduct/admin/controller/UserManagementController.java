package com.agriproduct.admin.controller;

import com.agriproduct.admin.dto.UserStatusRequest;
import com.agriproduct.admin.service.UserManagementService;
import com.agriproduct.common.core.domain.Result;
import com.agriproduct.user.entity.SysUser;
import com.agriproduct.user.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Result<IPage<UserInfoVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        IPage<UserInfoVO> result = userManagementService.getUserList(page);
        return Result.success(result);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateUserStatus(@PathVariable Long id,
                                             @RequestBody UserStatusRequest request) {
        Boolean result = userManagementService.updateUserStatus(id, request);
        return Result.success(result);
    }
}
