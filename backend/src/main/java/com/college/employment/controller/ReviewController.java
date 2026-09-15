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

import com.college.employment.application.employment.param.BatchReviewParam;
import com.college.employment.application.employment.ReviewAppService;
import com.college.employment.common.api.Result;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.AuditLog;
import com.college.employment.domain.model.EmploymentRecord;
import com.college.employment.infrastructure.mapper.AuditLogMapper;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 两级审核接口
 */
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewAppService reviewAppService;
    private final AuditLogMapper auditLogMapper;

    /**
     * 教师初审通过
     */
    @PostMapping("/api/teacher/review/approve")
    public Result<String> teacherApprove(
            Authentication authentication,
            @RequestParam Long recordId,
            @RequestParam(defaultValue = "") String comment) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        reviewAppService.teacherReview(recordId, "PASS", comment, userDetails.getUserId());
        return Result.ok("审核通过");
    }

    /**
     * 教师初审驳回
     */
    @PostMapping("/api/teacher/review/reject")
    public Result<String> teacherReject(
            Authentication authentication,
            @RequestParam Long recordId,
            @RequestParam(defaultValue = "") String comment) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        reviewAppService.teacherReview(recordId, "REJECT", comment, userDetails.getUserId());
        return Result.ok("已驳回");
    }

    /**
     * 管理员终审通过
     */
    @PostMapping("/api/admin/review/approve")
    public Result<String> adminApprove(
            Authentication authentication,
            @RequestParam Long recordId,
            @RequestParam(defaultValue = "") String comment) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        reviewAppService.adminReview(recordId, "PASS", comment, userDetails.getUserId());
        return Result.ok("终审通过");
    }

    /**
     * 管理员终审驳回
     */
    @PostMapping("/api/admin/review/reject")
    public Result<String> adminReject(
            Authentication authentication,
            @RequestParam Long recordId,
            @RequestParam(defaultValue = "") String comment) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        reviewAppService.adminReview(recordId, "REJECT", comment, userDetails.getUserId());
        return Result.ok("已驳回");
    }

    /**
     * 获取待初审列表（教师端，支持筛选）
     */
    @GetMapping("/api/teacher/review/pending")
    public Result<List<EmploymentRecord>> getFirstPending(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        LocalDateTime st = parseTime(startTime);
        LocalDateTime et = parseTime(endTime);
        List<EmploymentRecord> records = reviewAppService.getPendingReviews(
                "FIRST", userDetails.getDeptId(), name, companyName, destination, st, et);
        return Result.ok(records);
    }

    /**
     * 获取待终审列表（管理员端，支持筛选）
     */
    @GetMapping("/api/admin/review/pending")
    public Result<List<EmploymentRecord>> getFinalPending(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        // 管理员查看全部学院的
        LocalDateTime st = parseTime(startTime);
        LocalDateTime et = parseTime(endTime);
        List<EmploymentRecord> records = reviewAppService.getPendingReviews(
                "FINAL", null, name, companyName, destination, st, et);
        return Result.ok(records);
    }

    /**
     * 获取教师所在院系的待终审列表（教师端查看初审已通过记录）
     */
    @GetMapping("/api/teacher/review/final-pending")
    public Result<List<EmploymentRecord>> getTeacherFinalPending(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        LocalDateTime st = parseTime(startTime);
        LocalDateTime et = parseTime(endTime);
        List<EmploymentRecord> records = reviewAppService.getPendingReviews(
                "FINAL", userDetails.getDeptId(), name, companyName, destination, st, et);
        return Result.ok(records);
    }

    /**
     * 教师批量审核（通过/驳回）
     */
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/api/teacher/review/batch")
    public Result<String> teacherBatchReview(
            Authentication authentication,
            @RequestBody BatchReviewParam param) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        reviewAppService.batchReview(param.getRecordIds(), param.getAction(),
                param.getComment(), userDetails.getUserId(), true);
        return Result.ok("批量操作完成");
    }

    /**
     * 管理员批量审核（通过/驳回）
     */
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    @PostMapping("/api/admin/review/batch")
    public Result<String> adminBatchReview(
            Authentication authentication,
            @RequestBody BatchReviewParam param) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        reviewAppService.batchReview(param.getRecordIds(), param.getAction(),
                param.getComment(), userDetails.getUserId(), false);
        return Result.ok("批量操作完成");
    }

    /**
     * 查询某条记录的操作日志
     */
    @GetMapping("/api/review/audit-logs/{recordId}")
    public Result<List<AuditLog>> getAuditLogs(@PathVariable Long recordId) {
        List<AuditLog> logs = auditLogMapper.findByRecordId(recordId);
        return Result.ok(logs);
    }

    private LocalDateTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return null;
        if (timeStr.contains("T")) {
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return LocalDateTime.parse(timeStr + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
