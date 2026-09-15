/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.infrastructure.security;

import com.college.employment.config.JwtUserDetails;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 当前登录用户上下文工具
 *
 * 从 Spring Security 的 SecurityContextHolder 中解析 JWT 注入的 {@link JwtUserDetails}，
 * 供应用服务层获取操作人信息（用于数据权限过滤、操作日志留痕）。
 */
@Component
/** 当前登录用户上下文（touch） */
public class CurrentUserContext {
    /**
     * 获取当前登录用户信息
     */
    public Optional<JwtUserDetails> current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserDetails)) {
            return Optional.empty();
        }
        return Optional.of((JwtUserDetails) auth.getPrincipal());
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID（未登录时为 null）
     */
    public Long getUserId() {
        return current().map(JwtUserDetails::getUserId).orElse(null);
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名（未登录时为 system）
     */
    public String getUsername() {
        return current().map(JwtUserDetails::getUsername).orElse("system");
    }

    /**
     * 获取当前登录用户角色
     *
     * @return 角色（未登录时为 COLLEGE_ADMIN）
     */
    public String getRole() {
        return current().map(JwtUserDetails::getRole).orElse("COLLEGE_ADMIN");
    }

    /**
     * 是否为管理员（校管理员/系统管理员可见全量数据）
     */
    public boolean isAdmin() {
        String role = getRole();
        return "COLLEGE_ADMIN".equalsIgnoreCase(role) || "SYSTEM_ADMIN".equalsIgnoreCase(role);
    }
}
