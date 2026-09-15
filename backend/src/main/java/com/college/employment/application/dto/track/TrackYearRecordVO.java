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
 * 年度就业状态记录 VO（就业记录自动跟踪，按年取最新一条有效记录）
 */
@Data
public class TrackYearRecordVO {

    /** 年份 */
    private Integer year;

    /** 就业去向编码 */
    private String destination;

    /** 就业去向名称（签约就业/升学深造/出国留学/自主创业/灵活就业/待就业） */
    private String destinationName;

    /** 单位名称 */
    private String companyName;

    /** 职位 */
    private String position;

    /** 工作城市 */
    private String city;

    /** 薪资范围 */
    private String salaryRange;

    /** 记录提交时间 */
    private LocalDateTime recordTime;
}
