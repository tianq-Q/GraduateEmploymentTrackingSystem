/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.infrastructure.security;

import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 数据权限隔离辅助类
 *
 * 规则：
 * - 校级管理员(COLLEGE_ADMIN/SYSTEM_ADMIN)：可查看全部院系数据，返回空集合表示不限制；
 * - 教师(TEACHER)：仅可查看其所属院系(dept_id)的数据。
 *
 * 看板 SQL 已支持 deptIds 过滤。
 */
@Component
public class DataScopeHelper {
    private final CurrentUserContext currentUserContext;
    private final SysUserMapper sysUserMapper;

    public DataScopeHelper(CurrentUserContext currentUserContext, SysUserMapper sysUserMapper) {
        this.currentUserContext = currentUserContext;
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 获取当前用户可见的院系ID集合。
     * @return 空集合表示不限制（管理员）；否则仅返回该用户所属院系ID
     */
    public List<Long> resolveDeptIds() {
        if (currentUserContext.isAdmin()) {
            // 管理员不限制
            return Collections.emptyList();
        }
        // 非管理员：尝试从用户表取 deptId
        Long userId = currentUserContext.getUserId();
        if (userId == null) {
            return Collections.emptyList();
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeptId() == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(user.getDeptId());
    }
}
