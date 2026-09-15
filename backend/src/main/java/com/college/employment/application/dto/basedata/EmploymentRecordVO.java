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

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 就业申请记录 VO
 */
@Data
public class EmploymentRecordVO {

    /** 申请ID */
    private Long id;

    /** 就业类型 */
    private String destination;

    /** 就业类型名称 */
    private String destinationName;

    /** 单位名称 */
    private String companyName;

    /** 岗位名称 */
    private String position;

    /** 薪资范围 */
    private String salaryRange;

    /** 佐证材料URL */
    private String evidenceUrl;

    /** 审核状态 */
    private String reviewStatus;

    /** 审核状态名称 */
    private String reviewStatusName;

    /** 审核环节 */
    private String stage;

    /** 审核环节名称 */
    private String stageName;

    /** 提交时间 */
    private LocalDateTime createTime;
}
