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
import com.college.employment.application.basedata.DepartmentAppService;
import com.college.employment.application.dto.basedata.*;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 院系管理 Controller
 */
@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentAppService departmentAppService;

    /**
     * 分页查询院系
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<PageResult<DepartmentVO>> page(DepartmentQueryDTO query) {
        Page<DepartmentVO> page = departmentAppService.listPage(query);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    /**
     * 查询所有院系（下拉列表）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<List<DepartmentSimpleVO>> list() {
        return Result.ok(departmentAppService.listAll());
    }

    /**
     * 获取院系详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<DepartmentVO> getById(@PathVariable Long id) {
        return Result.ok(departmentAppService.getById(id));
    }

    /**
     * 新增院系
     */
    @PostMapping
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> create(@Valid @RequestBody DepartmentCreateDTO dto) {
        departmentAppService.create(dto);
        return Result.ok();
    }

    /**
     * 更新院系
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateDTO dto) {
        dto.setId(id);
        departmentAppService.update(dto);
        return Result.ok();
    }

    /**
     * 删除院系
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        departmentAppService.delete(id);
        return Result.ok();
    }

    /**
     * 院系统计
     * 权限：仅 ADMIN / TEACHER（学生不可查看他人聚合数据）
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<DepartmentStatisticsVO> statistics(@PathVariable Long id) {
        return Result.ok(departmentAppService.getStatistics(id));
    }
}
