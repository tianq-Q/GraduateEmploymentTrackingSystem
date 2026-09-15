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

package com.college.employment.common.constant;

/**
 * 系统角色常量
 */
public final class RoleConstants {

    public static final String GRADUATE = "GRADUATE";
    public static final String TEACHER = "TEACHER";
    public static final String COLLEGE_ADMIN = "COLLEGE_ADMIN";
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";

    /** Spring Security hasRole 表达式 */
    public static final String HAS_GRADUATE = "hasRole('GRADUATE')";
    public static final String HAS_TEACHER = "hasRole('TEACHER')";
    public static final String HAS_COLLEGE_ADMIN = "hasRole('COLLEGE_ADMIN')";
    public static final String HAS_SYSTEM_ADMIN = "hasRole('SYSTEM_ADMIN')";

    /** 组合权限表达式 */
    public static final String HAS_TEACHER_OR_COLLEGE = "hasAnyRole('TEACHER','COLLEGE_ADMIN')";
    public static final String HAS_COLLEGE_OR_SYSTEM = "hasAnyRole('COLLEGE_ADMIN','SYSTEM_ADMIN')";
    public static final String HAS_TEACHER_AND_ABOVE = "hasAnyRole('TEACHER','COLLEGE_ADMIN','SYSTEM_ADMIN')";
    public static final String HAS_COLLEGE_AND_ABOVE = "hasAnyRole('COLLEGE_ADMIN','SYSTEM_ADMIN')";
    public static final String HAS_ANY = "hasAnyRole('GRADUATE','TEACHER','COLLEGE_ADMIN','SYSTEM_ADMIN')";

    private RoleConstants() {}
}
