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

import lombok.Data;

/**
 * 提交就业状态变更 DTO
 */
@Data
public class StatusChangeSubmitDTO {

    /** 毕业生ID（学生本人提交时可不传，教师/管理员代录时必传） */
    private Long graduateId;

    /** 目标状态: UNEMPLOYED/PENDING/EMPLOYED/UNEMPLOYED_AFTER/POSTGRADUATE/ABROAD */
    private String toStatus;

    /** 失业原因（已就业→失业时必填：主动离职/企业裁员等） */
    private String reason;

    /** 佐证材料URL（升学录取通知书/离职证明等） */
    private String evidenceUrl;

    /** 备注 */
    private String remark;
}
