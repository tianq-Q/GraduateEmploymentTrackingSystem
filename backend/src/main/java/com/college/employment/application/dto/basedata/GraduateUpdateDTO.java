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

package com.college.employment.application.dto.basedata;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GraduateUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String gender;

    @NotNull(message = "所属院系不能为空")
    private Long deptId;

    @NotNull(message = "所属专业不能为空")
    private Long majorId;

    @NotNull(message = "所属班级不能为空")
    private Long classId;

    private String phone;

    private String email;
}
