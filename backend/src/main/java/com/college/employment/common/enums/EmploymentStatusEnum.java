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
 * 就业状态枚举
 */
@Getter
@AllArgsConstructor
public enum EmploymentStatusEnum {

    EMPLOYED("已就业", "已签约单位"),
    UNEMPLOYED("未就业", "尚未就业"),
    POSTGRADUATE("升学", "继续深造"),
    ENLISTED("入伍", "参军入伍"),
    ENTREPRENEURSHIP("创业", "自主创业"),
    UNEMPLOYED_WAITING("待业", "暂未就业");

    private final String label;
    private final String description;

    public static EmploymentStatusEnum fromDestination(String destination) {
        if (destination == null) return UNEMPLOYED;
        switch (destination) {
            case "签约就业": return EMPLOYED;
            case "升学": return POSTGRADUATE;
            case "出国": return POSTGRADUATE;
            case "入伍": return ENLISTED;
            case "创业": return ENTREPRENEURSHIP;
            case "灵活就业": return EMPLOYED;
            default: return UNEMPLOYED;
        }
    }
}
