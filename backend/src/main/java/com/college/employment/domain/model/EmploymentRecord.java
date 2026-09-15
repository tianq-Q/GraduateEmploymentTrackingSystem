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

package com.college.employment.domain.model;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 就业记录 领域实体 (核心聚合根)
 */
@Data
@TableName("employment_record")
public class EmploymentRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 毕业生ID */
    private Long graduateId;

    /** 单位名称 */
    private String companyName;

    /** 单位性质(国企/民企/外企等) */
    private String companyType;

    /** 所属行业 */
    private String industry;

    /** 职位 */
    private String position;

    /** 薪资范围 */
    private String salaryRange;

    /** 工作城市 */
    private String city;

    /** 去向: 签约就业/升学/出国/创业/灵活就业/待就业 */
    private String destination;

    /** 审核状态: PENDING/FIRST_PASSED/FIRST_REJECTED/APPROVED/FINAL_REJECTED */
    private String reviewStatus;

    /** 审核意见 */
    private String reviewComment;

    /** 审核人ID */
    private Long reviewerId;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    /** 是否代录 (管理员/教师代替学生录入) */
    private Boolean isProxy;

    /** 提交人ID (学生本人或代录的教师) */
    private Long submitterId;

    /** 提交人姓名 */
    private String submitterName;

    /** 佐证材料URL */
    private String evidenceUrl;

    /** 审核环节: TEACHER_REVIEW/ADMIN_REVIEW/COMPLETED/REJECTED */
    private String stage;

    /** 教师审核意见 */
    private String teacherComment;

    /** 管理员审核意见 */
    private String adminComment;

    /** 教师审核时间 */
    private LocalDateTime teacherReviewTime;

    /** 管理员审核时间 */
    private LocalDateTime adminReviewTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
}
