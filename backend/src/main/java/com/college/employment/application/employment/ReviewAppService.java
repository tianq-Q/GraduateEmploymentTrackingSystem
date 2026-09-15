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

import com.college.employment.domain.model.AuditLog;
import com.college.employment.domain.model.EmploymentRecord;
import com.college.employment.domain.model.EmploymentStatusChange;
import com.college.employment.domain.model.Graduate;
import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.AuditLogMapper;
import com.college.employment.infrastructure.mapper.EmploymentRecordMapper;
import com.college.employment.infrastructure.mapper.EmploymentStatusChangeMapper;
import com.college.employment.infrastructure.mapper.GraduateMapper;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 就业审核服务 - 两级审核（教师初审 + 管理员终审）
 *
 * 状态流转规则：
 *   PENDING → (教师初审通过) → FIRST_PASSED → (管理员终审通过) → APPROVED (生效)
 *   PENDING → (教师初审驳回) → FIRST_REJECTED → (学生修改重提) → PENDING
 *   FIRST_PASSED → (管理员终审驳回) → FINAL_REJECTED → (学生修改重提) → PENDING
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewAppService {

    private final EmploymentRecordMapper employmentRecordMapper;
    private final AuditLogMapper auditLogMapper;
    private final SysUserMapper sysUserMapper;
    private final GraduateMapper graduateMapper;
    private final EmploymentStatusChangeMapper employmentStatusChangeMapper;

    /**
     * 初审（教师审核本院系学生）
     * PENDING → FIRST_PASSED / FIRST_REJECTED
     */
    @Transactional
    public void teacherReview(Long recordId, String action, String comment, Long reviewerId) {
        EmploymentRecord record = getRecord(recordId);
        SysUser reviewer = getUser(reviewerId);

        if (!"PENDING".equals(record.getReviewStatus())) {
            throw new RuntimeException("该记录当前状态为【" + getStatusName(record.getReviewStatus()) + "】，不允许初审");
        }

        String newStatus;
        String logAction;
        String logComment;

        if ("PASS".equals(action)) {
            newStatus = "FIRST_PASSED";
            logAction = "FIRST_PASS";
            logComment = "教师初审通过" + (comment != null && !comment.isEmpty() ? ": " + comment : "");
        } else if ("REJECT".equals(action)) {
            newStatus = "FIRST_REJECTED";
            logAction = "FIRST_REJECT";
            logComment = "教师初审驳回" + (comment != null && !comment.isEmpty() ? ": " + comment : "");
        } else {
            throw new RuntimeException("无效的审核操作，只允许 PASS 或 REJECT");
        }

        record.setReviewStatus(newStatus);
        record.setReviewComment(comment);
        record.setReviewerId(reviewerId);
        record.setReviewTime(LocalDateTime.now());
        // 补齐分端字段与阶段（前端详情页依赖这些字段）
        record.setTeacherComment(comment);
        record.setTeacherReviewTime(LocalDateTime.now());
        record.setStage("REJECTED".equals(newStatus) ? "REJECTED" : "ADMIN_REVIEW");
        employmentRecordMapper.updateById(record);

        saveAuditLog(recordId, logAction, logComment, reviewerId, reviewer.getRealName(), reviewer.getRole());
        log.info("教师初审: recordId={}, action={}, reviewer={}", recordId, action, reviewer.getUsername());
    }

    /**
     * 终审（管理员审核通过初审的记录）
     * FIRST_PASSED → APPROVED / FINAL_REJECTED
     */
    @Transactional
    public void adminReview(Long recordId, String action, String comment, Long reviewerId) {
        EmploymentRecord record = getRecord(recordId);
        SysUser reviewer = getUser(reviewerId);

        if (!"FIRST_PASSED".equals(record.getReviewStatus())) {
            throw new RuntimeException("该记录当前状态为【" + getStatusName(record.getReviewStatus()) + "】，不允许终审（需先通过初审）");
        }

        String newStatus;
        String logAction;
        String logComment;

        if ("PASS".equals(action)) {
            newStatus = "APPROVED";
            logAction = "FINAL_PASS";
            logComment = "管理员终审通过" + (comment != null && !comment.isEmpty() ? ": " + comment : "");
        } else if ("REJECT".equals(action)) {
            newStatus = "FINAL_REJECTED";
            logAction = "FINAL_REJECT";
            logComment = "管理员终审驳回" + (comment != null && !comment.isEmpty() ? ": " + comment : "");
        } else {
            throw new RuntimeException("无效的审核操作，只允许 PASS 或 REJECT");
        }

        record.setReviewStatus(newStatus);
        record.setReviewComment(comment);
        record.setReviewerId(reviewerId);
        record.setReviewTime(LocalDateTime.now());
        // 补齐分端字段与阶段（前端详情页依赖这些字段）
        record.setAdminComment(comment);
        record.setAdminReviewTime(LocalDateTime.now());
        record.setStage("FINAL_REJECTED".equals(newStatus) ? "REJECTED" : "COMPLETED");
        employmentRecordMapper.updateById(record);

        saveAuditLog(recordId, logAction, logComment, reviewerId, reviewer.getRealName(), reviewer.getRole());
        // 终审通过 → 自动同步毕业生的就业跟踪状态为「已就业」，
        // 避免「就业登记已生效但跟踪状态仍为未就业」的数据不一致
        syncEmployedStatus(record, reviewer);
        log.info("管理员终审: recordId={}, action={}, reviewer={}", recordId, action, reviewer.getUsername());
    }

    /**
     * 查询待审核列表（支持筛选条件）
     */
    public List<EmploymentRecord> getPendingReviews(String reviewLevel, Long deptId,
                                                     String name, String companyName,
                                                     String destination,
                                                     LocalDateTime startTime, LocalDateTime endTime) {
        String reviewStatus = "FIRST".equals(reviewLevel) ? "PENDING" : "FIRST_PASSED";
        return employmentRecordMapper.findByReviewStatus(reviewStatus, deptId, name, companyName,
                destination, startTime, endTime);
    }

    /**
     * 批量审核（教师或管理员）
     */
    @Transactional
    public void batchReview(List<Long> recordIds, String action, String comment, Long reviewerId, boolean isTeacher) {
        for (Long recordId : recordIds) {
            if (isTeacher) {
                teacherReview(recordId, action, comment, reviewerId);
            } else {
                adminReview(recordId, action, comment, reviewerId);
            }
        }
    }

    /**
     * 终审通过后自动同步毕业生的就业跟踪状态为「已就业」。
     * <p>修复 bug：就业记录系统（终审通过）与跟踪状态系统（graduate.track_status）割裂，
     * 导致「就业登记已生效但学生就业状态页仍显示未就业」。本方法将二者联动：
     * <ul>
     *   <li>把对应 graduate.track_status 改为 EMPLOYED（已就业）</li>
     *   <li>往 employment_status_change 写一条变更记录，备注"就业登记终审通过自动同步"，
     *       学生/教师可在状态流转记录中追溯</li>
     * </ul>
     * 幂等：当前已是 EMPLOYED 跳过；graduate 不存在或无 graduateId 跳过（不影响审核主流程）。
     */
    private void syncEmployedStatus(EmploymentRecord record, SysUser reviewer) {
        if (record.getGraduateId() == null) {
            return;
        }
        Graduate graduate = graduateMapper.selectById(record.getGraduateId());
        if (graduate == null) {
            return;
        }
        String current = graduate.getTrackStatus();
        if ("EMPLOYED".equals(current)) {
            return;
        }
        String fromStatus = (current == null || current.isEmpty()) ? "UNEMPLOYED" : current;

        EmploymentStatusChange change = new EmploymentStatusChange();
        change.setGraduateId(graduate.getId());
        change.setFromStatus(fromStatus);
        change.setToStatus("EMPLOYED");
        change.setReason("就业登记终审通过自动同步");
        change.setRemark("来源：就业记录 #" + record.getId() + " - " + record.getCompanyName() + " / " + record.getPosition());
        change.setOperatorId(reviewer.getId());
        change.setOperatorName(reviewer.getRealName());
        change.setIsReminder(0);
        employmentStatusChangeMapper.insert(change);

        graduate.setTrackStatus("EMPLOYED");
        graduateMapper.updateById(graduate);
    }

    // ========== 内部工具方法 ==========

    private EmploymentRecord getRecord(Long recordId) {
        EmploymentRecord record = employmentRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("就业记录不存在");
        }
        return record;
    }

    private SysUser getUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
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
        log.setCreateTime(LocalDateTime.now());
        auditLogMapper.insert(log);
    }

    private String getStatusName(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "PENDING": return "待初审";
            case "FIRST_PASSED": return "初审通过(待终审)";
            case "FIRST_REJECTED": return "初审驳回";
            case "APPROVED": return "终审通过(已生效)";
            case "FINAL_REJECTED": return "终审驳回";
            case "WITHDRAWN": return "已撤回";
            default: return status;
        }
    }
}
