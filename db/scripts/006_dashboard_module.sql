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

-- =============================================================
-- 006 数据看板模块增量脚本
-- 适用项目：高校毕业生就业跟踪与分析系统
-- 说明：为 operation_log 补充看板操作日志所需字段
--      （description/operator_id/operator_name）
-- 注意：仅执行一次；若重复执行会因列已存在而报错，可忽略。
-- =============================================================

USE employment_tracking;

-- 操作描述
ALTER TABLE operation_log
    ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '操作描述' AFTER action;

-- 操作人ID
ALTER TABLE operation_log
    ADD COLUMN operator_id BIGINT DEFAULT NULL COMMENT '操作人ID' AFTER description;

-- 操作人姓名
ALTER TABLE operation_log
    ADD COLUMN operator_name VARCHAR(64) DEFAULT NULL COMMENT '操作人姓名' AFTER operator_id;
