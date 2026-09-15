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

import java.util.List;
import lombok.Data;

/**
 * 毕业生详情 VO（基本信息 + 就业记录 + 审核进度）
 */
@Data
public class GraduateDetailVO {

    /** 毕业生基本信息 */
    private GraduateVO basicInfo;

    /** 就业申请记录列表（按提交时间倒序） */
    private List<EmploymentRecordVO> employmentRecords;

    /** 最新一条申请的审核进度（无记录时为空） */
    private ReviewProgressVO reviewProgress;
}
