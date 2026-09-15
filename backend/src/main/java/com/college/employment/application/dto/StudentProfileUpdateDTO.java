/*
 * Copyright (c) 2026 EmploymentTracking Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.college.employment.application.dto;

import lombok.Data;

/**
 * 学生本人资料更新 DTO
 * 仅允许修改非关键字段（手机号、邮箱、性别）；学号、姓名、院系、专业、班级为只读，忽略前端提交
 */
@Data
public class StudentProfileUpdateDTO {

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 性别（男/女，本人可修改） */
    private String gender;
}
