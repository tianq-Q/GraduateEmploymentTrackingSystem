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

package com.college.employment.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态枚举
 *
 * 状态流转: PENDING → FIRST_PASSED → APPROVED
 *          PENDING → FIRST_REJECTED → (学生修改后) → PENDING
 *          FIRST_PASSED → FINAL_REJECTED → (学生修改后) → PENDING
 */
@Getter
@AllArgsConstructor
public enum ReviewStatusEnum {

    PENDING("待学院初审"),
    FIRST_PASSED("待学校终审"),
    FIRST_REJECTED("已驳回待修改"),
    APPROVED("审核通过"),
    FINAL_REJECTED("已驳回待修改"),
    WITHDRAWN("已撤回");

    private final String label;

    public static ReviewStatusEnum fromName(String name) {
        if (name == null) return null;
        for (ReviewStatusEnum e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
