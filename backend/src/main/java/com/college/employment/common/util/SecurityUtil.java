/*
 * MIT License
 *
 * Copyright (c) 2026 Employment Tracking System
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.college.employment.common.util;

import com.college.employment.common.exception.BusinessException;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类（基础信息模块）
 *
 * <p>适配当前项目角色体系：COLLEGE_ADMIN / SYSTEM_ADMIN / TEACHER / GRADUATE</p>
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    /** 学生（毕业生）角色：当前项目统一使用 GRADUATE */
    public static final String ROLE_STUDENT = "GRADUATE";

    /** 教师角色 */
    public static final String ROLE_TEACHER = "TEACHER";

    /** 院级管理员角色 */
    public static final String ROLE_ADMIN = "COLLEGE_ADMIN";

    private final SysUserMapper sysUserMapper;

    /**
     * 获取当前登录用户，未登录或会话失效时抛出 401
     */
    public JwtUserDetails requireLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserDetails)) {
            throw new BusinessException(401, "未认证或登录已过期，请重新登录");
        }
        return (JwtUserDetails) auth.getPrincipal();
    }

    /**
     * 获取当前用户角色（大写）
     */
    public String getRole() {
        JwtUserDetails current = requireLogin();
        return current.getRole() == null ? "" : current.getRole().toUpperCase();
    }

    /**
     * 是否管理员（院级/校级管理员）
     */
    public boolean isAdmin() {
        String role = getRole();
        return ROLE_ADMIN.equals(role) || "SYSTEM_ADMIN".equals(role);
    }

    /**
     * 学生（毕业生）返回当前登录学号；其他角色返回 null
     */
    public String getStudentNumber() {
        if (!ROLE_STUDENT.equalsIgnoreCase(getRole())) {
            return null;
        }
        return requireLogin().getUsername();
    }

    /**
     * 教师返回所属院系 ID；管理员/学生返回 null
     */
    public Long getTeacherDeptId() {
        if (!ROLE_TEACHER.equalsIgnoreCase(getRole())) {
            return null;
        }
        JwtUserDetails current = requireLogin();
        if (current.getDeptId() != null) {
            return current.getDeptId();
        }
        SysUser user = sysUserMapper.findByUsername(current.getUsername()).orElse(null);
        return user != null ? user.getDeptId() : null;
    }
}
