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

-- ============================================================
-- 008_add_deleted_columns.sql
-- 为 001_schema.sql 中缺失的列补齐
--   sys_user.deleted、department.description/deleted、
--   major.description/deleted、class_info.enrollment_year/graduation_year/description/deleted、
--   graduate.graduate_status/deleted、employment_record.is_proxy/submitter_id/submitter_name/
--   evidence_url/stage/teacher_comment/admin_comment/teacher_review_time/admin_review_time
--
-- 使用说明：
--   1) 本脚本与 001_schema.sql 配合，在新库初始化时按顺序执行（先 001 再 008）。
--   2) MySQL 8.0 不支持 `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`，
--      本脚本为纯静态 DDL，重复执行会因"列已存在"而失败。
--      如需在已有库上重跑，请先 DROP 已存在列或使用 Navicat 等工具。
-- ============================================================

USE employment_tracking;

-- sys_user
ALTER TABLE sys_user              ADD COLUMN deleted   TINYINT      DEFAULT 0          AFTER status;

-- department
ALTER TABLE department            ADD COLUMN description VARCHAR(500) DEFAULT NULL   AFTER status;
ALTER TABLE department            ADD COLUMN deleted   TINYINT      DEFAULT 0          AFTER description;

-- major
ALTER TABLE major                 ADD COLUMN description VARCHAR(500) DEFAULT NULL   AFTER status;
ALTER TABLE major                 ADD COLUMN deleted   TINYINT      DEFAULT 0          AFTER description;

-- class_info
ALTER TABLE class_info            ADD COLUMN enrollment_year  INT             DEFAULT NULL AFTER status;
ALTER TABLE class_info            ADD COLUMN graduation_year  INT             DEFAULT NULL AFTER enrollment_year;
ALTER TABLE class_info            ADD COLUMN description      VARCHAR(500)   DEFAULT NULL AFTER graduation_year;
ALTER TABLE class_info            ADD COLUMN deleted          TINYINT        DEFAULT 0    AFTER description;

-- graduate
ALTER TABLE graduate              ADD COLUMN graduate_status TINYINT        DEFAULT 1    AFTER employment_status;
ALTER TABLE graduate              ADD COLUMN deleted         TINYINT        DEFAULT 0    AFTER graduate_status;

-- employment_record: 补齐缺失的提交人/代录列 (001 schema) + 007/009/010 module additions
ALTER TABLE employment_record     ADD COLUMN is_proxy            TINYINT        DEFAULT 0    AFTER review_time;
ALTER TABLE employment_record     ADD COLUMN submitter_id        BIGINT         DEFAULT NULL AFTER is_proxy;
ALTER TABLE employment_record     ADD COLUMN submitter_name      VARCHAR(50)   DEFAULT NULL AFTER submitter_id;
ALTER TABLE employment_record     ADD COLUMN evidence_url        VARCHAR(500)  DEFAULT NULL AFTER submitter_name;
ALTER TABLE employment_record     ADD COLUMN stage               VARCHAR(30)   DEFAULT NULL AFTER evidence_url;
ALTER TABLE employment_record     ADD COLUMN teacher_comment     VARCHAR(500)  DEFAULT NULL AFTER stage;
ALTER TABLE employment_record     ADD COLUMN admin_comment       VARCHAR(500)  DEFAULT NULL AFTER teacher_comment;
ALTER TABLE employment_record     ADD COLUMN teacher_review_time DATETIME      DEFAULT NULL AFTER admin_comment;
ALTER TABLE employment_record     ADD COLUMN admin_review_time   DATETIME      DEFAULT NULL AFTER teacher_review_time;

-- Verification (should print 1 for the rows below)
SELECT 'sys_user.deleted'           AS column_check, COUNT(*) AS present FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='employment_tracking' AND TABLE_NAME='sys_user'             AND COLUMN_NAME='deleted'
UNION ALL SELECT 'department.deleted',          COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='employment_tracking' AND TABLE_NAME='department'         AND COLUMN_NAME='deleted'
UNION ALL SELECT 'major.deleted',               COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='employment_tracking' AND TABLE_NAME='major'              AND COLUMN_NAME='deleted'
UNION ALL SELECT 'class_info.deleted',          COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='employment_tracking' AND TABLE_NAME='class_info'         AND COLUMN_NAME='deleted'
UNION ALL SELECT 'graduate.deleted',            COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='employment_tracking' AND TABLE_NAME='graduate'           AND COLUMN_NAME='deleted';
