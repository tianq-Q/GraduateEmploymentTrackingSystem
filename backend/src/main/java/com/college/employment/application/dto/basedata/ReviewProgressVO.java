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
 * 最新一条就业申请的审核进度 VO
 */
@Data
public class ReviewProgressVO {

    /** 审核状态: PENDING/PASSED/REJECTED */
    private String reviewStatus;

    /** 审核状态名称 */
    private String reviewStatusName;

    /** 当前所处环节: TEACHER_REVIEW/ADMIN_REVIEW/COMPLETED/REJECTED */
    private String stage;

    /** 当前环节名称 */
    private String stageName;

    /** 教师意见 */
    private String teacherComment;

    /** 管理员意见 */
    private String adminComment;

    /** 教师审核时间 */
    private LocalDateTime teacherReviewTime;

    /** 管理员审核时间 */
    private LocalDateTime adminReviewTime;

    /** 申请提交时间 */
    private LocalDateTime createTime;
}
