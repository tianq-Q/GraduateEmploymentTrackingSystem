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

package com.college.employment.application.dto.track;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 就业状态变更记录 VO
 */
@Data
public class StatusChangeVO {

    private Long id;

    /** 毕业生ID */
    private Long graduateId;

    /** 学号 */
    private String studentNo;

    /** 姓名 */
    private String studentName;

    /** 院系ID */
    private Long deptId;

    /** 院系名称 */
    private String deptName;

    /** 变更前状态 */
    private String fromStatus;

    /** 变更前状态名称 */
    private String fromStatusName;

    /** 变更后状态 */
    private String toStatus;

    /** 变更后状态名称 */
    private String toStatusName;

    /** 失业原因 */
    private String reason;

    /** 佐证材料URL */
    private String evidenceUrl;

    /** 备注 */
    private String remark;

    /** 操作人姓名 */
    private String operatorName;

    /** 是否触发失业跟踪提醒(0否/1是) */
    private Integer isReminder;

    /** 变更时间 */
    private LocalDateTime createTime;
}
