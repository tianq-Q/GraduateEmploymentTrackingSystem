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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 毕业生 领域实体
 */
@Data
@TableName("graduate")
public class Graduate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学号 */
    @TableField("student_no")
    private String studentNumber;

    /** 姓名 */
    private String name;

    /** 性别 */
    private String gender;

    /** 身份证号 */
    private String idCard;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 所属院系ID */
    private Long deptId;

    /** 所属专业ID */
    private Long majorId;

    /** 所属班级ID */
    private Long classId;

    /** 年级 */
    private String grade;

    /** 毕业年份 */
    private String graduateYear;

    /** 就业状态: 待就业/已签约/升学/创业/出国/灵活就业/暂不就业 */
    @TableField("employment_status")
    private String status;

    /** 就业跟踪状态(六大类): UNEMPLOYED/PENDING/EMPLOYED/UNEMPLOYED_AFTER/POSTGRADUATE/ABROAD */
    @TableField("track_status")
    private String trackStatus;

    /** 毕业生状态: 1正常 0异常 */
    private Integer graduateStatus;

    /** 逻辑删除: 0正常 1删除 */
    @TableLogic
    private Integer deleted;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
}
