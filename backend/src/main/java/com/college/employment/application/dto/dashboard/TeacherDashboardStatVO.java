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

package com.college.employment.application.dto.dashboard;

import java.util.List;
import lombok.Data;

/**
 * 教师端本院系就业总览统计（touch）
 */
@Data
public class TeacherDashboardStatVO {

    /** 院系ID */
    private Long deptId;

    /** 院系名称 */
    private String deptName;

    /** 本院系毕业生总数 */
    private Long totalGraduates;

    /** 已就业人数 */
    private Long employedCount;

    /** 待就业人数 */
    private Long unemployedCount;

    /** 就业率（百分比，保留1位小数） */
    private Double employmentRate;

    /** 待审核就业申请数 */
    private Long pendingReviews;

    /** 就业状态分布（用于图表） */
    private List<NameValueVO> statusDistribution;
}
