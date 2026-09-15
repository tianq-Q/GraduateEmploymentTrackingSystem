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

package com.college.employment.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 当前登录用户的轻量信息载体（由 JWT 解析而来），
 * 作为 Spring Security 认证对象的 principal 使用。
 */
@Data
@AllArgsConstructor
public class JwtUserDetails {
    /** 用户主键 */
    private Long userId;
    /** 登录账号（学号/工号/管理账号） */
    private String username;
    /** 角色编码：SYSTEM_ADMIN / COLLEGE_ADMIN / TEACHER / GRADUATE */
    private String role;
    /** 所属院系 ID（系统管理员可为空） */
    private Long deptId;
}
