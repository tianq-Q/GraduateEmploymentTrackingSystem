/*
 * MIT License
 *
 * Copyright (c) 2026 Employment Tracking System
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.college.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.employment.application.dto.EmploymentSubmitDTO;
import com.college.employment.application.employment.EmploymentAppService;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.AuditLog;
import com.college.employment.domain.model.EmploymentRecord;
import com.college.employment.domain.model.Graduate;
import com.college.employment.infrastructure.mapper.AuditLogMapper;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 就业信息管理接口
 */
@RestController
@RequestMapping("/api/employment")
@RequiredArgsConstructor
public class EmploymentController {

    private final EmploymentAppService employmentAppService;
    private final AuditLogMapper auditLogMapper;

    /**
     * 学生提交就业信息
     */
    @PostMapping("/submit")
    public Result<EmploymentRecord> submit(Authentication authentication, @Valid @RequestBody EmploymentSubmitDTO dto) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        EmploymentRecord record = employmentAppService.submit(userDetails.getUserId(), dto);
        return Result.ok("提交成功", record);
    }

    /**
     * 学生撤回就业记录（仅 PENDING 状态）
     */
    @PostMapping("/withdraw/{recordId}")
    public Result<EmploymentRecord> withdraw(Authentication authentication, @PathVariable Long recordId) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        EmploymentRecord record = employmentAppService.withdraw(userDetails.getUserId(), recordId);
        return Result.ok("撤回成功", record);
    }

    /**
     * 学生修改并重新提交就业记录（从撤回/驳回状态）
     */
    @PutMapping("/resubmit/{recordId}")
    public Result<EmploymentRecord> resubmit(Authentication authentication,
                                              @PathVariable Long recordId,
                                              @Valid @RequestBody EmploymentSubmitDTO dto) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        EmploymentRecord record = employmentAppService.updateAndResubmit(userDetails.getUserId(), recordId, dto);
        return Result.ok("重新提交成功", record);
    }

    /**
     * 查询当前学生的就业记录
     */
    @GetMapping("/my-records")
    public Result<List<EmploymentRecord>> getMyRecords(Authentication authentication) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        List<EmploymentRecord> records = employmentAppService.getMyRecords(userDetails.getUserId());
        return Result.ok(records);
    }

    /**
     * 管理端 - 分页查询就业信息列表
     */
    @GetMapping("/list")
    public Result<PageResult<EmploymentRecord>> list(
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String reviewStatus,
            @RequestParam(required = false) Long deptId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<EmploymentRecord> result = employmentAppService.listPage(
                studentNo, name, companyName, destination, reviewStatus, deptId, page, size);
        return Result.ok(result);
    }

    /**
     * 批量查询毕业生信息
     */
    @GetMapping("/graduates")
    public Result<List<Graduate>> getGraduates(@RequestParam List<Long> ids) {
        List<Graduate> graduates = employmentAppService.getGraduatesByIds(ids);
        return Result.ok(graduates);
    }

    /**
     * 查询某条就业记录的操作日志
     */
    @GetMapping("/audit-logs/{recordId}")
    public Result<List<AuditLog>> getAuditLogs(@PathVariable Long recordId) {
        List<AuditLog> logs = auditLogMapper.findByRecordId(recordId);
        return Result.ok(logs);
    }

    /**
     * 查询所有操作日志（分页）
     */
    @GetMapping("/all-audit-logs")
    public Result<PageResult<AuditLog>> getAllAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AuditLog> pageParam = new Page<>(page, size);
        IPage<AuditLog> result = auditLogMapper.selectPage(pageParam,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AuditLog>()
                        .eq(action != null && !action.isEmpty(), AuditLog::getAction, action)
                        .orderByDesc(AuditLog::getCreateTime));
        return Result.ok(PageResult.of(result.getTotal(), page, size, result.getRecords()));
    }
}
