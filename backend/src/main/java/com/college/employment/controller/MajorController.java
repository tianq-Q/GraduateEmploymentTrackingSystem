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
import com.college.employment.application.basedata.MajorAppService;
import com.college.employment.application.dto.basedata.*;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 专业管理 Controller
 */
@RestController
@RequestMapping("/api/major")
@RequiredArgsConstructor
public class MajorController {

    private final MajorAppService majorAppService;

    /** 分页查询专业（支持关键字/院系筛选） */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<PageResult<MajorVO>> page(MajorQueryDTO query) {
        Page<MajorVO> page = majorAppService.listPage(query);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    /** 查询全部专业（下拉列表，可按院系过滤） */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<List<MajorSimpleVO>> list(@RequestParam(required = false) Long deptId) {
        return Result.ok(majorAppService.listByDeptId(deptId));
    }

    /** 新增专业 */
    @PostMapping
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> create(@Valid @RequestBody MajorCreateDTO dto) {
        majorAppService.create(dto);
        return Result.ok();
    }

    /** 更新专业 */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MajorUpdateDTO dto) {
        dto.setId(id);
        majorAppService.update(dto);
        return Result.ok();
    }

    /** 删除专业（存在班级时拒绝） */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        majorAppService.delete(id);
        return Result.ok();
    }

    /** 专业维度统计（班级数/毕业生数/就业率） */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<MajorStatisticsVO> statistics(@PathVariable Long id) {
        return Result.ok(majorAppService.getStatistics(id));
    }
}
