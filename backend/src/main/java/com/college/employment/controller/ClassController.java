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
import com.college.employment.application.basedata.ClassAppService;
import com.college.employment.application.dto.basedata.*;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 班级管理 Controller
 */
@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassAppService classAppService;

    /** 分页查询班级（支持关键字/专业/院系筛选） */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<PageResult<ClassVO>> page(ClassQueryDTO query) {
        Page<ClassVO> page = classAppService.listPage(query);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    /** 查询全部班级（下拉列表，可按专业过滤） */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<List<ClassSimpleVO>> list(@RequestParam(required = false) Long majorId) {
        return Result.ok(classAppService.listByMajorId(majorId));
    }

    /** 新增班级 */
    @PostMapping
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> create(@Valid @RequestBody ClassCreateDTO dto) {
        classAppService.create(dto);
        return Result.ok();
    }

    /** 更新班级 */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ClassUpdateDTO dto) {
        dto.setId(id);
        classAppService.update(dto);
        return Result.ok();
    }

    /** 删除班级（存在毕业生时拒绝） */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        classAppService.delete(id);
        return Result.ok();
    }

    /** 班级维度统计（毕业生数/就业率） */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<ClassStatisticsVO> statistics(@PathVariable Long id) {
        return Result.ok(classAppService.getStatistics(id));
    }
}
