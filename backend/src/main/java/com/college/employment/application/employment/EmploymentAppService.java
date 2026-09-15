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

package com.college.employment.application.employment;

import com.college.employment.application.dto.EmploymentSubmitDTO;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.enums.ReviewStatusEnum;
import com.college.employment.domain.model.AuditLog;
import com.college.employment.domain.model.EmploymentRecord;
import com.college.employment.domain.model.Graduate;
import com.college.employment.domain.model.SysUser;
import com.college.employment.domain.repository.EmploymentRecordRepository;
import com.college.employment.infrastructure.mapper.AuditLogMapper;
import com.college.employment.infrastructure.mapper.EmploymentRecordMapper;
import com.college.employment.infrastructure.mapper.GraduateMapper;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 就业登记应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentAppService {

    private final EmploymentRecordRepository employmentRecordRepository;
    private final EmploymentRecordMapper employmentRecordMapper;
    private final GraduateMapper graduateMapper;
    private final SysUserMapper sysUserMapper;
    private final AuditLogMapper auditLogMapper;

    /**
     * 学生自主提交就业信息
     */
    @Transactional
    public EmploymentRecord submit(Long currentUserId, EmploymentSubmitDTO dto) {
        SysUser user = getUser(currentUserId);
        Graduate graduate = findOrCreateGraduate(user, dto.getStudentNo());

        // 检查是否有审核中的记录，如果有则不允许重复提交
        List<EmploymentRecord> existingRecords = employmentRecordRepository.findByGraduateId(graduate.getId());
        for (EmploymentRecord r : existingRecords) {
            if ("PENDING".equals(r.getReviewStatus()) || "FIRST_PASSED".equals(r.getReviewStatus())) {
                throw new RuntimeException("你有一条审核中的就业记录，请先撤回后再提交新记录");
            }
        }

        EmploymentRecord record = buildRecord(dto, graduate);
        record.setReviewStatus(ReviewStatusEnum.PENDING.name());
        record.setIsProxy(false);
        record.setSubmitterId(currentUserId);
        record.setSubmitterName(user.getRealName());

        employmentRecordRepository.save(record);

        // 记录操作日志
        saveAuditLog(record.getId(), "SUBMIT", "学生自主提交就业信息", currentUserId,
                user.getRealName(), user.getRole());

        log.info("学生 {} 提交了就业信息, graduateId={}, recordId={}", user.getUsername(), graduate.getId(), record.getId());
        return record;
    }

    /**
     * 撤回就业记录（仅 PENDING 状态可撤回）
     */
    @Transactional
    public EmploymentRecord withdraw(Long currentUserId, Long recordId) {
        SysUser user = getUser(currentUserId);
        EmploymentRecord record = getRecord(recordId);

        // 验证权限：只能撤回自己的记录
        if (!record.getSubmitterId().equals(currentUserId)) {
            throw new RuntimeException("只能撤回自己提交的就业记录");
        }

        // 只能撤回待审核状态的记录
        if (!"PENDING".equals(record.getReviewStatus())) {
            throw new RuntimeException("只能撤回待审核状态的就业记录，当前状态: " + record.getReviewStatus());
        }

        record.setReviewStatus("WITHDRAWN");
        employmentRecordRepository.updateById(record);

        saveAuditLog(record.getId(), "WITHDRAW", "学生撤回就业记录", currentUserId,
                user.getRealName(), user.getRole());

        log.info("学生 {} 撤回了就业记录 recordId={}", user.getUsername(), recordId);
        return record;
    }

    /**
     * 修改并重新提交就业记录（从撤回状态或驳回状态）
     */
    @Transactional
    public EmploymentRecord updateAndResubmit(Long currentUserId, Long recordId, EmploymentSubmitDTO dto) {
        SysUser user = getUser(currentUserId);
        EmploymentRecord record = getRecord(recordId);

        // 验证权限
        if (!record.getSubmitterId().equals(currentUserId)) {
            throw new RuntimeException("只能修改自己提交的就业记录");
        }

        // 只能修改撤回状态或驳回状态的记录
        if (!"WITHDRAWN".equals(record.getReviewStatus()) &&
                !"FIRST_REJECTED".equals(record.getReviewStatus()) &&
                !"FINAL_REJECTED".equals(record.getReviewStatus())) {
            throw new RuntimeException("当前状态不允许修改: " + record.getReviewStatus());
        }

        // 更新字段
        record.setDestination(dto.getDestination());
        record.setCompanyName(dto.getCompanyName());
        record.setCompanyType(dto.getCompanyType());
        record.setIndustry(dto.getIndustry());
        record.setPosition(dto.getPosition());
        record.setSalaryRange(dto.getSalaryRange());
        record.setCity(dto.getCity());
        record.setReviewStatus(ReviewStatusEnum.PENDING.name());
        record.setReviewComment(null);
        record.setReviewerId(null);
        record.setReviewTime(null);

        employmentRecordRepository.updateById(record);

        saveAuditLog(record.getId(), "UPDATE", "学生修改后重新提交就业信息", currentUserId,
                user.getRealName(), user.getRole());

        log.info("学生 {} 修改并重新提交了就业记录 recordId={}", user.getUsername(), recordId);
        return record;
    }

    /**
     * 查询当前学生的就业记录
     * 优先按学号匹配毕业生档案（与提交逻辑一致），姓名兜底；
     * 最后按提交人兜底（覆盖代录等场景），避免匹配失败导致记录查不到。
     */
    public List<EmploymentRecord> getMyRecords(Long currentUserId) {
        SysUser user = sysUserMapper.selectById(currentUserId);
        if (user == null) return Collections.emptyList();

        // 1) 学号优先：student_number，空则用登录账号名
        String studentNo = (user.getStudentNumber() != null && !user.getStudentNumber().trim().isEmpty()) ?
                user.getStudentNumber().trim() : user.getUsername();
        Graduate graduate = null;
        if (studentNo != null && !studentNo.isEmpty()) {
            graduate = graduateMapper.findByStudentNo(studentNo);
        }
        // 2) 姓名兜底
        if (graduate == null && user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            graduate = graduateMapper.findByName(user.getRealName().trim());
        }
        if (graduate != null) {
            List<EmploymentRecord> records = employmentRecordRepository.findByGraduateId(graduate.getId());
            if (!records.isEmpty()) return records;
        }
        // 3) 提交人兜底（含代录场景）
        return employmentRecordMapper.findBySubmitterId(currentUserId);
    }

    /**
     * 分页查询就业信息列表（管理端）
     */
    public PageResult<EmploymentRecord> listPage(String studentNo, String name, String companyName,
                                                  String destination, String reviewStatus, Long deptId,
                                                  int page, int size) {
        int offset = (page - 1) * size;
        List<EmploymentRecord> records = employmentRecordMapper.findPage(
                studentNo, name, companyName, destination, reviewStatus, deptId, offset, size);
        long total = employmentRecordMapper.countPage(
                studentNo, name, companyName, destination, reviewStatus, deptId);
        return PageResult.of(total, page, size, records);
    }

    /**
     * 批量查询毕业生信息
     */
    public List<Graduate> getGraduatesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return graduateMapper.findByIds(ids);
    }

    // ========== 内部工具方法 ==========

    private SysUser getUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    private EmploymentRecord getRecord(Long recordId) {
        return employmentRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("就业记录不存在"));
    }

    private EmploymentRecord buildRecord(EmploymentSubmitDTO dto, Graduate graduate) {
        EmploymentRecord record = new EmploymentRecord();
        record.setGraduateId(graduate.getId());
        record.setDestination(dto.getDestination());
        record.setCompanyName(dto.getCompanyName());
        record.setCompanyType(dto.getCompanyType());
        record.setIndustry(dto.getIndustry());
        record.setPosition(dto.getPosition());
        record.setSalaryRange(dto.getSalaryRange());
        record.setCity(dto.getCity());
        return record;
    }

    private Graduate findOrCreateGraduate(SysUser user, String studentNo) {
        String finalStudentNo = (studentNo != null && !studentNo.trim().isEmpty()) ?
                studentNo.trim() : user.getUsername();

        if (finalStudentNo != null && !finalStudentNo.isEmpty()) {
            Graduate existing = graduateMapper.findByStudentNo(finalStudentNo);
            if (existing != null) return existing;
        }

        String realName = user.getRealName();
        if (realName != null && !realName.trim().isEmpty()) {
            Graduate existing = graduateMapper.findByName(realName.trim());
            if (existing != null) return existing;
        }

        Graduate graduate = new Graduate();
        graduate.setStudentNumber(finalStudentNo);
        graduate.setName(realName != null && !realName.trim().isEmpty() ? realName.trim() : user.getUsername());
        graduate.setPhone(user.getPhone());
        graduate.setEmail(user.getEmail());
        // 账号未绑定院系时兜底默认院系，避免 NOT NULL 约束导致插入失败
        Long deptId = user.getDeptId();
        graduate.setDeptId(deptId != null ? deptId : 1L);
        graduate.setMajorId(1L);
        graduate.setClassId(1L);
        graduate.setGrade("2022");
        graduate.setGraduateYear("2024");
        graduate.setStatus("待就业");
        graduateMapper.insert(graduate);
        log.info("自动创建毕业生档案: studentNo={}, name={}", graduate.getStudentNumber(), graduate.getName());
        return graduate;
    }

    private void saveAuditLog(Long recordId, String action, String comment, Long operatorId,
                               String operatorName, String operatorRole) {
        AuditLog log = new AuditLog();
        log.setRecordId(recordId);
        log.setAction(action);
        log.setComment(comment);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperatorRole(operatorRole);
        auditLogMapper.insert(log);
    }
}
