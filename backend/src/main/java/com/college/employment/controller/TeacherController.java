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

package com.college.employment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.employment.application.basedata.TeacherAppService;
import com.college.employment.application.dto.basedata.*;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 教师账号管理 Controller
 */
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherAppService teacherAppService;

    /** 分页查询教师（支持关键字/院系筛选） */
    @GetMapping("/page")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<PageResult<TeacherVO>> page(TeacherQueryDTO query) {
        Page<TeacherVO> page = teacherAppService.listPage(query);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    /** 新增教师账号（自动同步创建登录账号） */
    @PostMapping
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> create(@Valid @RequestBody TeacherCreateDTO dto) {
        teacherAppService.create(dto);
        return Result.ok();
    }

    /** 更新教师信息 */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TeacherUpdateDTO dto) {
        dto.setId(id);
        teacherAppService.update(dto);
        return Result.ok();
    }

    /** 启用/停用教师账号 */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        teacherAppService.toggleStatus(id);
        return Result.ok();
    }
}
