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
-- 009_track_module.sql 毕业生跟踪功能
-- 按毕业年份(届)设置跟踪年限(3年/5年)，系统自动计算跟踪进度，
-- 并基于 employment_record 就业记录按年度自动聚合就业状态变化。
-- 可重复执行（幂等）。
-- ============================================================

CREATE TABLE IF NOT EXISTS track_setting (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  graduate_year  VARCHAR(10)  NOT NULL COMMENT '毕业年份(如2025)',
  track_years    INT          NOT NULL DEFAULT 3 COMMENT '跟踪年限(3或5)',
  create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_graduate_year (graduate_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='毕业生跟踪设置(按届)';

-- 种子数据：为已有毕业年份补默认 3 年跟踪（不覆盖已有设置）
INSERT INTO track_setting (graduate_year, track_years)
SELECT DISTINCT graduate_year, 3
FROM graduate
WHERE graduate_year IS NOT NULL AND CHAR_LENGTH(graduate_year) > 0 AND deleted = 0
ON DUPLICATE KEY UPDATE track_years = track_years;
