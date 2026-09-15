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
-- 就业状态流转管理模块
-- 六大状态：UNEMPLOYED(未就业) / PENDING(待审核) / EMPLOYED(已就业)
--           / UNEMPLOYED_AFTER(失业) / POSTGRADUATE(升学) / ABROAD(出国)
-- 状态变更需提交佐证材料；已就业→失业自动触发跟踪提醒教师
-- 说明：graduate.employment_status 字段已存在且存中文值，为避免破坏现有功能，
--       此处新增独立的 track_status 字段存六大类枚举值。
-- ============================================================

-- 1. graduate 表新增就业跟踪状态字段（六大类枚举值）
ALTER TABLE graduate ADD COLUMN track_status VARCHAR(30) DEFAULT 'UNEMPLOYED' COMMENT '就业跟踪状态:UNEMPLOYED未就业/PENDING待审核/EMPLOYED已就业/UNEMPLOYED_AFTER失业/POSTGRADUATE升学/ABROAD出国';

-- 2. 就业状态变更记录表
CREATE TABLE IF NOT EXISTS employment_status_change (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    graduate_id BIGINT NOT NULL COMMENT '毕业生ID',
    from_status VARCHAR(30) NOT NULL COMMENT '变更前状态',
    to_status VARCHAR(30) NOT NULL COMMENT '变更后状态',
    reason VARCHAR(100) DEFAULT NULL COMMENT '失业原因(主动离职/企业裁员等)',
    evidence_url VARCHAR(500) DEFAULT NULL COMMENT '佐证材料URL(录取通知书/离职证明等)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    is_reminder TINYINT DEFAULT 0 COMMENT '是否触发失业跟踪提醒(0否/1是)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    INDEX idx_graduate (graduate_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就业状态变更记录';
