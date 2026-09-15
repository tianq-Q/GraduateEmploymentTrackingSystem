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

/**
 * 就业跟踪状态（六大类）
 */
public enum TrackStatusEnum {

    /** 未就业 */
    UNEMPLOYED("未就业"),
    /** 待审核 */
    PENDING("待审核"),
    /** 已就业 */
    EMPLOYED("已就业"),
    /** 失业（已就业后失业，需跟踪） */
    UNEMPLOYED_AFTER("失业"),
    /** 升学 */
    POSTGRADUATE("升学"),
    /** 出国 */
    ABROAD("出国");

    private final String label;

    TrackStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isValid(String status) {
        for (TrackStatusEnum s : values()) {
            if (s.name().equals(status)) {
                return true;
            }
        }
        return false;
    }

    public static String labelOf(String status) {
        if (status == null) {
            return "";
        }
        for (TrackStatusEnum s : values()) {
            if (s.name().equals(status)) {
                return s.label;
            }
        }
        return status;
    }
}
