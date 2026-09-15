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
import com.college.employment.application.basedata.GraduateAppService;
import com.college.employment.application.dto.basedata.*;
import com.college.employment.application.dto.StudentProfileUpdateDTO;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 毕业生管理 Controller
 */
@RestController
@RequestMapping("/api/graduate")
@RequiredArgsConstructor
public class GraduateController {

    private final GraduateAppService graduateAppService;

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<PageResult<GraduateVO>> page(GraduateQueryDTO query) {
        Page<GraduateVO> page = graduateAppService.listPage(query);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<GraduateVO> getById(@PathVariable Long id) {
        return Result.ok(graduateAppService.getById(id));
    }

    /**
     * 学生获取本人基本信息（自动绑定当前登录学号，前端无需传身份参数）
     * 权限：仅 STUDENT
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('GRADUATE','STUDENT')")
    public Result<GraduateVO> getMyProfile() {
        return Result.ok(graduateAppService.getMyProfile());
    }

    /**
     * 学生更新本人基本信息（仅手机号、邮箱等非关键字段可编辑，其余只读）
     * 权限：仅 STUDENT
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('GRADUATE','STUDENT')")
    public Result<Void> updateMyProfile(@RequestBody StudentProfileUpdateDTO dto) {
        graduateAppService.updateMyProfile(dto);
        return Result.ok();
    }

    /**
     * 毕业生完整详情：基本信息 + 就业记录列表 + 最新审核进度
     * 权限：ADMIN 任意；TEACHER 本院系；STUDENT 本人（方法内校验）
     */
    @GetMapping("/{id}/detail")
    public Result<GraduateDetailVO> getDetail(@PathVariable Long id) {
        return Result.ok(graduateAppService.getDetail(id));
    }

    /**
     * 分页查询某毕业生的就业申请历史记录
     * 权限：ADMIN 任意；TEACHER 本院系；STUDENT 本人（方法内校验）
     */
    @GetMapping("/{id}/employment-history")
    public Result<PageResult<EmploymentRecordVO>> employmentHistory(@PathVariable Long id, PageQueryDTO query) {
        Page<EmploymentRecordVO> page = graduateAppService.getEmploymentHistory(id, query);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @PostMapping
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> create(@Valid @RequestBody GraduateCreateDTO dto) {
        graduateAppService.create(dto);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody GraduateUpdateDTO dto) {
        dto.setId(id);
        graduateAppService.update(dto);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        graduateAppService.toggleStatus(id);
        return Result.ok();
    }
}
