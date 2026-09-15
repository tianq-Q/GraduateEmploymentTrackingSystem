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

import java.util.List;
import lombok.Data;

/**
 * 毕业生跟踪详情 VO
 */
@Data
public class TrackDetailVO {

    /** 毕业生ID */
    private Long graduateId;

    /** 学号 */
    private String studentNumber;

    /** 姓名 */
    private String name;

    /** 毕业年份 */
    private String graduateYear;

    /** 跟踪年限（3/5） */
    private Integer trackYears;

    /** 跟踪开始年份（= 毕业年份） */
    private Integer startYear;

    /** 跟踪结束年份（= 毕业年份 + 年限） */
    private Integer endYear;

    /** 当前年份 */
    private Integer currentYear;

    /** 已跟踪年数 */
    private Integer trackedYears;

    /** 剩余年数 */
    private Integer remainingYears;

    /** 跟踪状态：TRACKING 跟踪中 / FINISHED 已结束 */
    private String status;

    /** 跟踪状态名称 */
    private String statusName;

    /** 跟踪进度（0~1，已跟踪年数/年限） */
    private Double progress;

    /** 年度就业状态时间线（按年倒序） */
    private List<TrackYearRecordVO> yearRecords;
}
