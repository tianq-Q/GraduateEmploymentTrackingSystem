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

import com.college.employment.application.dto.EmploymentSubmitDTO;
import com.college.employment.common.api.Result;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.*;
import com.college.employment.infrastructure.mapper.*;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 信息代录接口（辅导员/管理员后台代替学生录入就业信息）
 */
@Slf4j
@RestController
@RequestMapping("/api/teacher/proxy")
@RequiredArgsConstructor
public class ProxyController {

    private final SysUserMapper sysUserMapper;
    private final GraduateMapper graduateMapper;
    private final EmploymentRecordMapper employmentRecordMapper;
    private final AuditLogMapper auditLogMapper;

    /**
     * 获取可代录的学生列表（教师：本院学生；管理员：全部学生）
     */
    @GetMapping("/students")
    public Result<List<SysUser>> getStudents(Authentication authentication) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        List<SysUser> students;
        if ("COLLEGE_ADMIN".equals(userDetails.getRole()) || "SYSTEM_ADMIN".equals(userDetails.getRole())) {
            // 管理员可以代录全部学生
            students = sysUserMapper.findByRole("GRADUATE");
        } else {
            // 教师只能代录本院学生
            students = sysUserMapper.findByRoleAndDept("GRADUATE", userDetails.getDeptId());
        }
        return Result.ok(students);
    }

    /**
     * 管理员/教师代为提交就业信息
     */
    @PostMapping("/submit")
    @Transactional
    public Result<EmploymentRecord> proxySubmit(Authentication authentication,
                                                 @Valid @RequestBody EmploymentSubmitDTO dto) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        Long proxyUserId = userDetails.getUserId();

        // 查询代录操作人信息
        SysUser proxyUser = sysUserMapper.selectById(proxyUserId);
        if (proxyUser == null) {
            return Result.fail("操作人不存在");
        }

        // 查找或创建目标学生
        Graduate graduate = findOrCreateGraduate(dto.getStudentNo(), dto.getName());

        // 权限校验：教师只能代录本院学生
        if ("TEACHER".equals(userDetails.getRole())) {
            if (graduate.getDeptId() != null && !graduate.getDeptId().equals(userDetails.getDeptId())) {
                return Result.fail("只能代录本院学生");
            }
        }

        // 检查是否已有审核中的记录
        List<EmploymentRecord> existing = employmentRecordMapper.findByGraduateId(graduate.getId());
        for (EmploymentRecord r : existing) {
            if ("PENDING".equals(r.getReviewStatus()) || "FIRST_PASSED".equals(r.getReviewStatus())) {
                return Result.fail("该学生有一条审核中的就业记录，请先处理后再代录");
            }
        }

        // 创建代录记录
        EmploymentRecord record = new EmploymentRecord();
        record.setGraduateId(graduate.getId());
        record.setDestination(dto.getDestination());
        record.setCompanyName(dto.getCompanyName());
        record.setCompanyType(dto.getCompanyType());
        record.setIndustry(dto.getIndustry());
        record.setPosition(dto.getPosition());
        record.setSalaryRange(dto.getSalaryRange());
        record.setCity(dto.getCity());
        record.setReviewStatus("PENDING");
        record.setIsProxy(true);
        record.setSubmitterId(proxyUserId);
        record.setSubmitterName(proxyUser.getRealName());
        employmentRecordMapper.insert(record);

        // 记录代录操作日志
        AuditLog auditLog = new AuditLog();
        auditLog.setRecordId(record.getId());
        auditLog.setAction("PROXY_SUBMIT");
        auditLog.setComment("管理员/教师代录就业信息: " + graduate.getName() + "(" + graduate.getStudentNumber() + ")");
        auditLog.setOperatorId(proxyUserId);
        auditLog.setOperatorName(proxyUser.getRealName());
        auditLog.setOperatorRole(userDetails.getRole());
        auditLogMapper.insert(auditLog);

        log.info("{} {} 代录了就业信息: graduateId={}, recordId={}",
                userDetails.getRole(), proxyUser.getUsername(), graduate.getId(), record.getId());
        return Result.ok("代录成功", record);
    }

    private Graduate findOrCreateGraduate(String studentNo, String name) {
        if (studentNo != null && !studentNo.trim().isEmpty()) {
            Graduate g = graduateMapper.findByStudentNo(studentNo.trim());
            if (g != null) return g;
        }
        if (name != null && !name.trim().isEmpty()) {
            Graduate g = graduateMapper.findByName(name.trim());
            if (g != null) return g;
        }

        // 自动创建
        Graduate graduate = new Graduate();
        graduate.setStudentNumber(studentNo != null ? studentNo.trim() : "");
        graduate.setName(name != null ? name.trim() : "");
        graduate.setMajorId(1L);
        graduate.setClassId(1L);
        graduate.setGrade("2022");
        graduate.setGraduateYear("2024");
        graduate.setStatus("UNEMPLOYED");
        graduateMapper.insert(graduate);
        return graduate;
    }
}
