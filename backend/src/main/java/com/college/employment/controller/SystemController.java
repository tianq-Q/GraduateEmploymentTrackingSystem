/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.college.employment.application.dto.system.UserPageQueryDTO;
import com.college.employment.application.dto.system.UserVO;
import com.college.employment.application.system.SystemAppService;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理模块 REST 控制器（仅系统管理员）
 *
 * <p>系统管理员只负责：账号启停（主任/教师/毕业生）与系统日志，不参与任何业务管理。</p>
 */
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemAppService systemAppService;

    /**
     * 分页查询账号（可按角色/关键字筛选）
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public Result<PageResult<UserVO>> pageUsers(UserPageQueryDTO query) {
        IPage<UserVO> page = systemAppService.pageUsers(query);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    /**
     * 启用/禁用账号
     *
     * @param status 1=启用 0=禁用
     */
    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        systemAppService.updateStatus(id, status);
        return Result.ok(null);
    }
}
