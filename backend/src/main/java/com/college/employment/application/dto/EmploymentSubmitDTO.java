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

package com.college.employment.application.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 就业登记请求 DTO（前端提交）
 */
@Data
public class EmploymentSubmitDTO {

    /** 学号（自动从当前登录用户获取，代录时需传入） */
    private String studentNo;

    /** 姓名（代录时传入） */
    private String name;

    /** 去向: 签约就业/升学/出国/创业/灵活就业/待就业 */
    @NotBlank(message = "毕业去向不能为空")
    private String destination;

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
}
