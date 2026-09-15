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

-- 004_update_roles.sql
-- 更新角色体系: GRADUATE, TEACHER, COLLEGE_ADMIN, SYSTEM_ADMIN

-- 迁移现有角色
-- STUDENT -> GRADUATE
UPDATE employment_tracking.sys_user SET role = 'GRADUATE' WHERE role = 'STUDENT';

-- ADMIN -> SYSTEM_ADMIN (如果之前有 admin 账号)
UPDATE employment_tracking.sys_user SET role = 'SYSTEM_ADMIN' WHERE role = 'ADMIN' AND student_number = 'admin';

-- 如果还有其他 ADMIN 角色 -> COLLEGE_ADMIN
UPDATE employment_tracking.sys_user SET role = 'COLLEGE_ADMIN' WHERE role = 'ADMIN';

-- 更新 role 字段注释
ALTER TABLE employment_tracking.sys_user MODIFY COLUMN role VARCHAR(20) NOT NULL DEFAULT 'GRADUATE' 
    COMMENT '角色: GRADUATE-毕业生, TEACHER-教师, COLLEGE_ADMIN-校级管理员, SYSTEM_ADMIN-系统管理员';

-- 确认结果
SELECT 'Role migration completed' AS status;
