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
