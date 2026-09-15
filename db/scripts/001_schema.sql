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

-- ==========================================
-- 就业跟踪系统 - 完整建表脚本
-- ==========================================
-- 执行前请先创建数据库 employment_tracking：
--   推荐使用 setup-db.bat（Windows）或 setup-db.sh（Mac/Linux）一键初始化
--   也可手动执行: CREATE DATABASE employment_tracking DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


-- 1. 系统用户表
CREATE TABLE sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(200) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    student_number VARCHAR(50)  DEFAULT NULL COMMENT '学号/工号',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar      VARCHAR(200) DEFAULT NULL COMMENT '头像URL',
    role        VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT '角色: ADMIN/TEACHER/STUDENT',
    dept_id     BIGINT       DEFAULT NULL COMMENT '所属院系ID',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role (role),
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB COMMENT='系统用户表';

-- 2. 院系表
CREATE TABLE department (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '院系名称',
    code        VARCHAR(20)  NOT NULL UNIQUE COMMENT '院系编码',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='院系表';

-- 3. 专业表
CREATE TABLE major (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '专业名称',
    code        VARCHAR(20)  NOT NULL UNIQUE COMMENT '专业编码',
    dept_id     BIGINT       NOT NULL COMMENT '所属院系ID',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB COMMENT='专业表';

-- 4. 班级表
CREATE TABLE class_info (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '班级名称',
    major_id    BIGINT       NOT NULL COMMENT '所属专业ID',
    grade       VARCHAR(10)  NOT NULL COMMENT '年级(如2022)',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_major_id (major_id)
) ENGINE=InnoDB COMMENT='班级表';

-- 5. 毕业生表
CREATE TABLE graduate (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no    VARCHAR(30)  NOT NULL UNIQUE COMMENT '学号',
    name          VARCHAR(50)  NOT NULL COMMENT '姓名',
    gender        VARCHAR(10)  DEFAULT NULL COMMENT '性别',
    id_card       VARCHAR(20)  DEFAULT NULL COMMENT '身份证号',
    phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    dept_id       BIGINT       NOT NULL COMMENT '院系ID',
    major_id      BIGINT       NOT NULL COMMENT '专业ID',
    class_id      BIGINT       NOT NULL COMMENT '班级ID',
    grade         VARCHAR(10)  NOT NULL COMMENT '年级',
    graduate_year VARCHAR(10)  NOT NULL COMMENT '毕业年份',
    employment_status VARCHAR(20) DEFAULT 'UNEMPLOYED' COMMENT '就业状态',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dept_id (dept_id),
    INDEX idx_major_id (major_id),
    INDEX idx_class_id (class_id),
    INDEX idx_status (employment_status)
) ENGINE=InnoDB COMMENT='毕业生表';

-- 6. 就业记录表
CREATE TABLE employment_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    graduate_id     BIGINT       NOT NULL COMMENT '毕业生ID',
    company_name    VARCHAR(200) DEFAULT NULL COMMENT '单位名称',
    company_type    VARCHAR(50)  DEFAULT NULL COMMENT '单位性质(国企/民企/外企等)',
    industry        VARCHAR(50)  DEFAULT NULL COMMENT '所属行业',
    position        VARCHAR(100) DEFAULT NULL COMMENT '职位',
    salary_range    VARCHAR(50)  DEFAULT NULL COMMENT '薪资范围',
    city            VARCHAR(50)  DEFAULT NULL COMMENT '工作城市',
    destination     VARCHAR(30)  NOT NULL COMMENT '去向: 签约就业/升学/出国/创业/灵活就业/待就业',
    review_status   VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态: PENDING/FIRST_PASSED/FIRST_REJECTED/APPROVED/FINAL_REJECTED',
    review_comment  VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    reviewer_id     BIGINT       DEFAULT NULL COMMENT '审核人ID',
    review_time     DATETIME     DEFAULT NULL COMMENT '审核时间',
    is_proxy        TINYINT      DEFAULT 0 COMMENT '是否代录: 0否 1是',
    submitter_id    BIGINT       DEFAULT NULL COMMENT '提交人ID',
    submitter_name  VARCHAR(50)  DEFAULT NULL COMMENT '提交人姓名',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_graduate_id (graduate_id),
    INDEX idx_review_status (review_status),
    INDEX idx_destination (destination)
) ENGINE=InnoDB COMMENT='就业记录表';

-- 7. 附件表
CREATE TABLE attachment (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id   BIGINT       NOT NULL COMMENT '关联记录ID',
    file_name   VARCHAR(200) NOT NULL COMMENT '原始文件名',
    file_path   VARCHAR(300) NOT NULL COMMENT '存储路径',
    file_size   BIGINT       DEFAULT 0 COMMENT '文件大小(字节)',
    file_type   VARCHAR(50)  DEFAULT NULL COMMENT '文件类型',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB COMMENT='附件表';

-- 8. 审核日志表
CREATE TABLE audit_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id   BIGINT       NOT NULL COMMENT '就业记录ID',
    action      VARCHAR(30)  NOT NULL COMMENT '操作: SUBMIT/WITHDRAW/UPDATE/FIRST_PASS/FIRST_REJECT/FINAL_PASS/FINAL_REJECT/PROXY_SUBMIT',
    comment     VARCHAR(500) DEFAULT NULL COMMENT '审核意见/操作说明',
    operator_id BIGINT       NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    operator_role VARCHAR(20) DEFAULT NULL COMMENT '操作人角色',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB COMMENT='审核日志表';

-- 9. 登录日志表
CREATE TABLE login_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       DEFAULT NULL COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL COMMENT '登录用户名',
    ip          VARCHAR(50)  DEFAULT NULL COMMENT '登录IP',
    user_agent  VARCHAR(500) DEFAULT NULL COMMENT '浏览器UA',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1成功 0失败',
    message     VARCHAR(200) DEFAULT NULL COMMENT '消息',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB COMMENT='登录日志表';

-- 10. 操作日志表
CREATE TABLE operation_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       DEFAULT NULL COMMENT '操作用户ID',
    username    VARCHAR(50)  DEFAULT NULL COMMENT '操作用户名',
    module      VARCHAR(50)  DEFAULT NULL COMMENT '操作模块',
    action      VARCHAR(100) DEFAULT NULL COMMENT '操作描述',
    method      VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params      TEXT         DEFAULT NULL COMMENT '请求参数',
    ip          VARCHAR(50)  DEFAULT NULL COMMENT '请求IP',
    duration    BIGINT       DEFAULT 0 COMMENT '耗时(ms)',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB COMMENT='操作日志表';

-- 11. 字典类型表
CREATE TABLE dict_type (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE COMMENT '字典编码',
    name        VARCHAR(100) NOT NULL COMMENT '字典名称',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='字典类型表';

-- 12. 字典项表
CREATE TABLE dict_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_id     BIGINT       NOT NULL COMMENT '字典类型ID',
    label       VARCHAR(100) NOT NULL COMMENT '字典标签',
    value       VARCHAR(100) NOT NULL COMMENT '字典值',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type_id (type_id)
) ENGINE=InnoDB COMMENT='字典项表';

-- 13. 通知表
CREATE TABLE notification (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID',
    title       VARCHAR(200) NOT NULL COMMENT '通知标题',
    content     TEXT         DEFAULT NULL COMMENT '通知内容',
    is_read     TINYINT      NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB COMMENT='通知表';

-- 14. 系统配置表
CREATE TABLE sys_config (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key   VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(500) DEFAULT NULL COMMENT '配置值',
    description VARCHAR(200) DEFAULT NULL COMMENT '配置说明',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='系统配置表';
