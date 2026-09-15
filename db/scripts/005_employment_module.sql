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

-- 005_employment_module.sql
-- 就业信息模块表结构增量迁移: 补齐 employment_record / audit_log 缺失列

-- employment_record: 补充代录相关列
ALTER TABLE employment_record ADD COLUMN is_proxy TINYINT DEFAULT 0 COMMENT '是否代录: 0否 1是' AFTER review_time;
ALTER TABLE employment_record ADD COLUMN submitter_id BIGINT DEFAULT NULL COMMENT '提交人ID' AFTER is_proxy;
ALTER TABLE employment_record ADD COLUMN submitter_name VARCHAR(50) DEFAULT NULL COMMENT '提交人姓名' AFTER submitter_id;

-- employment_record: 更新审核状态注释（两级审核流）
ALTER TABLE employment_record MODIFY COLUMN review_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
    COMMENT '审核状态: PENDING/FIRST_PASSED/FIRST_REJECTED/APPROVED/FINAL_REJECTED';

-- audit_log: action 扩长 + 补充操作人信息列
ALTER TABLE audit_log MODIFY COLUMN action VARCHAR(30) NOT NULL
    COMMENT '操作: SUBMIT/WITHDRAW/UPDATE/FIRST_PASS/FIRST_REJECT/FINAL_PASS/FINAL_REJECT/PROXY_SUBMIT';
ALTER TABLE audit_log ADD COLUMN operator_name VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名' AFTER operator_id;
ALTER TABLE audit_log ADD COLUMN operator_role VARCHAR(20) DEFAULT NULL COMMENT '操作人角色' AFTER operator_name;

-- 确认结果
SELECT 'Employment module migration completed' AS status;
