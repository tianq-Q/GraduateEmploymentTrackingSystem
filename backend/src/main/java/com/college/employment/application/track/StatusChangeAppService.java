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

package com.college.employment.application.track;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.college.employment.application.dto.track.StatusChangeSubmitDTO;
import com.college.employment.application.dto.track.StatusChangeVO;
import com.college.employment.common.enums.TrackStatusEnum;
import com.college.employment.common.exception.BusinessException;
import com.college.employment.common.util.SecurityUtil;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.Department;
import com.college.employment.domain.model.EmploymentStatusChange;
import com.college.employment.domain.model.Graduate;
import com.college.employment.domain.model.Notification;
import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.DepartmentMapper;
import com.college.employment.infrastructure.mapper.EmploymentStatusChangeMapper;
import com.college.employment.infrastructure.mapper.GraduateMapper;
import com.college.employment.infrastructure.mapper.NotificationMapper;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 就业状态流转 应用服务
 *
 * <p>六大状态：未就业/待审核/已就业/失业/升学/出国。
 * 状态变更需提交佐证材料（如升学录取通知书、离职证明）；
 * 从「已就业」变更为「失业」时自动触发跟踪流程：
 * 向该生所属院系的教师推送「失业学生跟踪提醒」，并记录失业原因。</p>
 */
@Service
@RequiredArgsConstructor
public class StatusChangeAppService {

    private final GraduateMapper graduateMapper;
    private final EmploymentStatusChangeMapper statusChangeMapper;
    private final SysUserMapper sysUserMapper;
    private final NotificationMapper notificationMapper;
    private final DepartmentMapper departmentMapper;
    private final SecurityUtil securityUtil;

    /** 失业原因字典：value -> label（预置，允许扩展） */
    private static final Map<String, String> UNEMPLOYED_REASONS = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("VOLUNTARY_QUIT", "主动离职");
                put("LAYOFF", "企业裁员");
                put("OTHER", "其他");
            }});

    /**
     * 提交就业状态变更
     * 权限：GRADUATE 本人；TEACHER/ADMIN 代录本院系/任意学生
     */
    @Transactional
    public StatusChangeVO submitChange(StatusChangeSubmitDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserDetails)) {
            throw new BusinessException(401, "未认证或登录已过期，请重新登录");
        }
        JwtUserDetails current = (JwtUserDetails) auth.getPrincipal();
        String role = current.getRole() == null ? "" : current.getRole().toUpperCase();
        String operatorName = current.getUsername();
        SysUser currentUser = sysUserMapper.findByUsername(current.getUsername()).orElse(null);
        if (currentUser != null && currentUser.getRealName() != null) {
            operatorName = currentUser.getRealName();
        }

        // 1. 定位毕业生
        Graduate graduate;
        if ("GRADUATE".equals(role)) {
            String studentNumber = securityUtil.getStudentNumber();
            graduate = graduateMapper.selectOne(new LambdaQueryWrapper<Graduate>()
                    .eq(Graduate::getStudentNumber, studentNumber));
            if (graduate == null) {
                throw new BusinessException(404, "未找到本人的毕业生档案，请联系管理员");
            }
        } else if ("TEACHER".equals(role) || "COLLEGE_ADMIN".equals(role) || "SYSTEM_ADMIN".equals(role)) {
            if (dto.getGraduateId() == null) {
                throw new BusinessException(400, "代录状态变更必须指定毕业生");
            }
            graduate = graduateMapper.selectById(dto.getGraduateId());
            if (graduate == null) {
                throw new BusinessException(404, "毕业生不存在");
            }
            if ("TEACHER".equals(role)) {
                SysUser user = sysUserMapper.findByUsername(current.getUsername()).orElse(null);
                Long teacherDeptId = user != null ? user.getDeptId() : null;
                if (teacherDeptId == null || !teacherDeptId.equals(graduate.getDeptId())) {
                    throw new BusinessException(403, "无权操作其他院系毕业生的状态");
                }
            }
        } else {
            throw new BusinessException(403, "无权限执行此操作");
        }

        // 2. 校验目标状态
        String toStatus = dto.getToStatus();
        if (!StringUtils.hasText(toStatus) || !TrackStatusEnum.isValid(toStatus)) {
            throw new BusinessException(400, "目标就业状态不合法");
        }

        String fromStatus = graduate.getTrackStatus();
        if (fromStatus == null) {
            fromStatus = TrackStatusEnum.UNEMPLOYED.name();
        }
        if (fromStatus.equals(toStatus)) {
            throw new BusinessException(400, "就业状态未发生变化");
        }

        // 3. 佐证材料：升学/出国/失业必须提交
        boolean needEvidence = TrackStatusEnum.POSTGRADUATE.name().equals(toStatus) ||
                TrackStatusEnum.ABROAD.name().equals(toStatus) ||
                TrackStatusEnum.UNEMPLOYED_AFTER.name().equals(toStatus);
        if (needEvidence && !StringUtils.hasText(dto.getEvidenceUrl())) {
            throw new BusinessException(400, "该状态变更必须提交佐证材料（如录取通知书、离职证明）");
        }

        // 4. 已就业 → 失业：必须填写失业原因，且自动触发跟踪提醒
        boolean unemployedReminder = TrackStatusEnum.EMPLOYED.name().equals(fromStatus) &&
                TrackStatusEnum.UNEMPLOYED_AFTER.name().equals(toStatus);
        if (unemployedReminder && !StringUtils.hasText(dto.getReason())) {
            throw new BusinessException(400, "变更为「失业」必须填写失业原因（主动离职/企业裁员等）");
        }

        // 5. 更新毕业生跟踪状态
        graduate.setTrackStatus(toStatus);
        graduateMapper.updateById(graduate);

        // 6. 记录变更
        EmploymentStatusChange change = new EmploymentStatusChange();
        change.setGraduateId(graduate.getId());
        change.setFromStatus(fromStatus);
        change.setToStatus(toStatus);
        change.setReason(dto.getReason());
        change.setEvidenceUrl(dto.getEvidenceUrl());
        change.setRemark(dto.getRemark());
        change.setOperatorId(current.getUserId());
        change.setOperatorName(operatorName);
        change.setIsReminder(unemployedReminder ? 1 : 0);
        statusChangeMapper.insert(change);

        // 7. 失业自动提醒本院系教师
        if (unemployedReminder) {
            notifyTeachersUnemployed(graduate, dto.getReason());
        }

        return toVO(change, graduate);
    }

    /**
     * 向毕业生所属院系的教师推送「失业学生跟踪提醒」
     */
    private void notifyTeachersUnemployed(Graduate graduate, String reason) {
        List<SysUser> teachers = sysUserMapper.findByRoleAndDept("TEACHER", graduate.getDeptId());
        if (teachers.isEmpty()) {
            return;
        }
        String reasonLabel = UNEMPLOYED_REASONS.getOrDefault(reason, reason);
        String title = "失业学生跟踪提醒";
        String content = String.format(
                "【失业学生跟踪提醒】学生 %s（学号：%s）已由「已就业」变更为「失业」，失业原因：%s，请及时开展跟踪帮扶。",
                graduate.getName(), graduate.getStudentNumber(), reasonLabel);
        for (SysUser teacher : teachers) {
            Notification notification = new Notification();
            notification.setUserId(teacher.getId());
            notification.setTitle(title);
            notification.setContent(content);
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }
    }

    /**
     * 学生查询本人的状态变更历史
     */
    public List<StatusChangeVO> listMyChanges() {
        String studentNumber = securityUtil.getStudentNumber();
        if (studentNumber == null) {
            throw new BusinessException(403, "仅学生可查询本人的状态变更");
        }
        Graduate graduate = graduateMapper.selectOne(new LambdaQueryWrapper<Graduate>()
                .eq(Graduate::getStudentNumber, studentNumber));
        if (graduate == null) {
            throw new BusinessException(404, "未找到本人的毕业生档案");
        }
        return listChangesByGraduate(graduate.getId());
    }

    /**
     * 教师/管理员查询状态变更记录（教师仅本院系，管理员全部）
     */
    public List<StatusChangeVO> listChanges() {
        String role = securityUtil.getRole();
        if ("TEACHER".equals(role)) {
            Long deptId = securityUtil.getTeacherDeptId();
            List<Long> graduateIds = graduateMapper.selectList(new LambdaQueryWrapper<Graduate>()
                            .eq(Graduate::getDeptId, deptId))
                    .stream().map(Graduate::getId).collect(Collectors.toList());
            if (graduateIds.isEmpty()) {
                return new ArrayList<>();
            }
            return toVOList(statusChangeMapper.selectList(new LambdaQueryWrapper<EmploymentStatusChange>()
                    .in(EmploymentStatusChange::getGraduateId, graduateIds)
                    .orderByDesc(EmploymentStatusChange::getCreateTime)));
        }
        if ("COLLEGE_ADMIN".equals(role) || "SYSTEM_ADMIN".equals(role)) {
            return toVOList(statusChangeMapper.selectList(new LambdaQueryWrapper<EmploymentStatusChange>()
                    .orderByDesc(EmploymentStatusChange::getCreateTime)));
        }
        throw new BusinessException(403, "无权限查询状态变更记录");
    }

    /**
     * 教师端：失业学生跟踪提醒列表（本院系）
     */
    public List<StatusChangeVO> listUnemployedReminders() {
        String role = securityUtil.getRole();
        if (!"TEACHER".equals(role) && !"COLLEGE_ADMIN".equals(role) && !"SYSTEM_ADMIN".equals(role)) {
            throw new BusinessException(403, "仅教师/管理员可查看失业跟踪提醒");
        }
        LambdaQueryWrapper<EmploymentStatusChange> wrapper = new LambdaQueryWrapper<EmploymentStatusChange>()
                .eq(EmploymentStatusChange::getIsReminder, 1)
                .orderByDesc(EmploymentStatusChange::getCreateTime);
        if ("TEACHER".equals(role)) {
            Long deptId = securityUtil.getTeacherDeptId();
            List<Long> graduateIds = graduateMapper.selectList(new LambdaQueryWrapper<Graduate>()
                            .eq(Graduate::getDeptId, deptId))
                    .stream().map(Graduate::getId).collect(Collectors.toList());
            if (graduateIds.isEmpty()) {
                return new ArrayList<>();
            }
            wrapper.in(EmploymentStatusChange::getGraduateId, graduateIds);
        }
        return toVOList(statusChangeMapper.selectList(wrapper));
    }

    /**
     * 学生当前就业跟踪状态
     */
    public Map<String, Object> getMyStatus() {
        String studentNumber = securityUtil.getStudentNumber();
        if (studentNumber == null) {
            throw new BusinessException(403, "仅学生可查询本人的状态");
        }
        Graduate graduate = graduateMapper.selectOne(new LambdaQueryWrapper<Graduate>()
                .eq(Graduate::getStudentNumber, studentNumber));
        if (graduate == null) {
            throw new BusinessException(404, "未找到本人的毕业生档案");
        }
        String status = graduate.getTrackStatus();
        if (status == null) {
            status = TrackStatusEnum.UNEMPLOYED.name();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("graduateId", graduate.getId());
        result.put("studentNo", graduate.getStudentNumber());
        result.put("name", graduate.getName());
        result.put("status", status);
        result.put("statusName", TrackStatusEnum.labelOf(status));
        return result;
    }

    /**
     * 查询某毕业生的变更历史
     */
    public List<StatusChangeVO> listChangesByGraduate(Long graduateId) {
        return toVOList(statusChangeMapper.selectList(new LambdaQueryWrapper<EmploymentStatusChange>()
                .eq(EmploymentStatusChange::getGraduateId, graduateId)
                .orderByDesc(EmploymentStatusChange::getCreateTime)));
    }

    private List<StatusChangeVO> toVOList(List<EmploymentStatusChange> changes) {
        if (changes == null || changes.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> graduateIds = changes.stream().map(EmploymentStatusChange::getGraduateId)
                .distinct().collect(Collectors.toList());
        Map<Long, Graduate> graduateMap = graduateMapper.selectBatchIds(graduateIds).stream()
                .collect(Collectors.toMap(Graduate::getId, g -> g));
        Map<Long, String> deptNames = new HashMap<>();
        for (Graduate g : graduateMap.values()) {
            if (g.getDeptId() != null && !deptNames.containsKey(g.getDeptId())) {
                Department dept = departmentMapper.selectById(g.getDeptId());
                deptNames.put(g.getDeptId(), dept != null ? dept.getName() : "");
            }
        }
        return changes.stream().map(c -> {
            Graduate g = graduateMap.get(c.getGraduateId());
            return toVO(c, g, deptNames.get(g != null ? g.getDeptId() : null));
        }).collect(Collectors.toList());
    }

    private StatusChangeVO toVO(EmploymentStatusChange change, Graduate graduate) {
        String deptName = "";
        if (graduate != null && graduate.getDeptId() != null) {
            Department dept = departmentMapper.selectById(graduate.getDeptId());
            deptName = dept != null ? dept.getName() : "";
        }
        return toVO(change, graduate, deptName);
    }

    private StatusChangeVO toVO(EmploymentStatusChange change, Graduate graduate, String deptName) {
        StatusChangeVO vo = new StatusChangeVO();
        vo.setId(change.getId());
        vo.setGraduateId(change.getGraduateId());
        if (graduate != null) {
            vo.setStudentNo(graduate.getStudentNumber());
            vo.setStudentName(graduate.getName());
            vo.setDeptId(graduate.getDeptId());
            vo.setDeptName(deptName);
        }
        vo.setFromStatus(change.getFromStatus());
        vo.setFromStatusName(TrackStatusEnum.labelOf(change.getFromStatus()));
        vo.setToStatus(change.getToStatus());
        vo.setToStatusName(TrackStatusEnum.labelOf(change.getToStatus()));
        vo.setReason(change.getReason());
        vo.setEvidenceUrl(change.getEvidenceUrl());
        vo.setRemark(change.getRemark());
        vo.setOperatorName(change.getOperatorName());
        vo.setIsReminder(change.getIsReminder());
        vo.setCreateTime(change.getCreateTime());
        return vo;
    }
}
