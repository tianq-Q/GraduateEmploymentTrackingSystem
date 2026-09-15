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

package com.college.employment.application.basedata;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.employment.application.dto.basedata.*;
import com.college.employment.common.exception.BusinessException;
import com.college.employment.common.util.SecurityUtil;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.*;
import com.college.employment.infrastructure.mapper.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 毕业生管理 应用服务
 */
@Service
@RequiredArgsConstructor
public class GraduateAppService {

    private final GraduateMapper graduateMapper;
    private final DepartmentMapper departmentMapper;
    private final MajorMapper majorMapper;
    private final ClassInfoMapper classInfoMapper;
    private final SysUserMapper sysUserMapper;
    private final EmploymentRecordMapper employmentRecordMapper;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    /** 去向类型字典：value -> label */
    private static final Map<String, String> DESTINATION_NAMES = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("SIGNED", "签约就业");
                put("FURTHER_STUDY", "升学深造");
                put("ABROAD", "出国留学");
                put("ENTREPRENEURSHIP", "自主创业");
                put("FLEXIBLE", "灵活就业");
                put("WAITING", "待就业");
            }});

    /** 审核状态字典：value -> label */
    private static final Map<String, String> REVIEW_STATUS_NAMES = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("PENDING", "待审核");
                put("PASSED", "审核通过");
                put("APPROVED", "审核通过");
                put("REJECTED", "审核退回");
            }});

    /** 审核环节字典：value -> label */
    private static final Map<String, String> STAGE_NAMES = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("TEACHER_REVIEW", "待教师审核");
        put("ADMIN_REVIEW", "待管理员审核");
        put("COMPLETED", "审核完成");
        put("REJECTED", "已退回");
    }});

    /**
     * 分页查询毕业生列表
     */
    public Page<GraduateVO> listPage(GraduateQueryDTO query) {
        Page<Graduate> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Graduate> wrapper = new LambdaQueryWrapper<>();

        // 学生数据隔离：列表查询接口对学生直接禁用，学生仅可访问本人资料接口
        String studentNumber = securityUtil.getStudentNumber();
        if (studentNumber != null) {
            throw new BusinessException(403, "列表查询接口对学生禁用，请通过个人中心查看本人信息");
        }
        // 教师数据隔离：仅返回本院系毕业生，强制覆盖前端传入的院系条件
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null) {
            query.setDeptId(teacherDeptId);
        }
        if (query.getDeptId() != null) {
            wrapper.eq(Graduate::getDeptId, query.getDeptId());
        }
        if (query.getMajorId() != null) {
            wrapper.eq(Graduate::getMajorId, query.getMajorId());
        }
        if (query.getClassId() != null) {
            wrapper.eq(Graduate::getClassId, query.getClassId());
        }
        if (query.getGraduationYear() != null) {
            LambdaQueryWrapper<ClassInfo> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(ClassInfo::getGraduationYear, query.getGraduationYear());
            List<Long> classIds = classInfoMapper.selectList(classWrapper).stream()
                    .map(ClassInfo::getId).collect(Collectors.toList());
            if (!classIds.isEmpty()) {
                wrapper.in(Graduate::getClassId, classIds);
            } else {
                // 如果该毕业年份没有班级，返回空页
                Page<GraduateVO> empty = new Page<>(query.getPage(), query.getSize(), 0);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Graduate::getStatus, query.getStatus());
        }

        String keyword = query.getKeyword();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Graduate::getStudentNumber, keyword)
                    .or().like(Graduate::getName, keyword));
        }
        if (StringUtils.hasText(query.getStudentNumber())) {
            wrapper.like(Graduate::getStudentNumber, query.getStudentNumber());
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Graduate::getName, query.getName());
        }
        wrapper.orderByDesc(Graduate::getId);

        Page<Graduate> result = graduateMapper.selectPage(page, wrapper);

        // 获取关联信息
        Map<Long, String> deptMap = departmentMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        Map<Long, Major> majorMap = majorMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Major::getId, m -> m));
        Map<Long, ClassInfo> classMap = classInfoMapper.selectAllActive().stream()
                .collect(Collectors.toMap(ClassInfo::getId, c -> c));

        List<GraduateVO> voList = result.getRecords().stream().map(g -> {
            GraduateVO vo = new GraduateVO();
            BeanUtils.copyProperties(g, vo);
            vo.setDeptName(deptMap.getOrDefault(g.getDeptId(), ""));
            Major major = majorMap.get(g.getMajorId());
            vo.setMajorName(major != null ? major.getName() : "");
            ClassInfo cls = classMap.get(g.getClassId());
            vo.setClassName(cls != null ? cls.getName() : "");
            return vo;
        }).collect(Collectors.toList());

        Page<GraduateVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 新增毕业生（同时创建学生账号）
     */
    @Transactional
    public void create(GraduateCreateDTO dto) {
        // 唯一性校验
        if (graduateMapper.countByStudentNumber(dto.getStudentNumber()) > 0) {
            throw new BusinessException("学号已存在");
        }

        Department dept = departmentMapper.selectById(dto.getDeptId());
        if (dept == null) {
            throw new BusinessException("所属院系不存在");
        }
        Major major = majorMapper.selectById(dto.getMajorId());
        if (major == null) {
            throw new BusinessException("所属专业不存在");
        }
        ClassInfo cls = classInfoMapper.selectById(dto.getClassId());
        if (cls == null) {
            throw new BusinessException("所属班级不存在");
        }

        // 创建毕业生记录
        Graduate graduate = new Graduate();
        BeanUtils.copyProperties(dto, graduate);
        graduate.setStatus("待就业");
        graduate.setGraduateStatus(1);
        // graduate 表 grade/graduate_year 为 NOT NULL 且无默认值，
        // 从所选班级自动带出年级与毕业年份（用户无需重复填写）
        if (StringUtils.hasText(cls.getGrade())) {
            graduate.setGrade(cls.getGrade());
        }
        if (cls.getGraduationYear() != null) {
            graduate.setGraduateYear(String.valueOf(cls.getGraduationYear()));
        }
        graduateMapper.insert(graduate);

        // 学生账号：已有同号账号时复用（仅同步基础信息，不重置密码），否则自动创建（初始密码 123456）
        SysUser existing = sysUserMapper.findByUsername(dto.getStudentNumber()).orElse(null);
        if (existing != null) {
            if (!SecurityUtil.ROLE_STUDENT.equals(existing.getRole())) {
                throw new BusinessException("该学号已被注册为" + existing.getRole() + "角色用户，无法补建毕业生档案");
            }
            SysUser update = new SysUser();
            update.setId(existing.getId());
            update.setRealName(dto.getName());
            update.setPhone(dto.getPhone());
            update.setEmail(dto.getEmail());
            update.setDeptId(dto.getDeptId());
            sysUserMapper.updateById(update);
        } else {
            SysUser user = new SysUser();
            user.setUsername(dto.getStudentNumber());
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRealName(dto.getName());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setRole(SecurityUtil.ROLE_STUDENT);
            user.setDeptId(dto.getDeptId());
            user.setStatus(1);
            user.setAvatar(null);
            sysUserMapper.insert(user);
        }
    }

    /**
     * 更新毕业生信息
     */
    @Transactional
    public void update(GraduateUpdateDTO dto) {
        Graduate existing = graduateMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("毕业生不存在");
        }

        Department dept = departmentMapper.selectById(dto.getDeptId());
        if (dept == null) {
            throw new BusinessException("所属院系不存在");
        }
        Major major = majorMapper.selectById(dto.getMajorId());
        if (major == null) {
            throw new BusinessException("所属专业不存在");
        }
        ClassInfo cls = classInfoMapper.selectById(dto.getClassId());
        if (cls == null) {
            throw new BusinessException("所属班级不存在");
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setStudentNumber(existing.getStudentNumber()); // 不更新学号
        existing.setGraduateStatus(existing.getGraduateStatus()); // 不更新账号状态
        graduateMapper.updateById(existing);

        // 同步更新用户信息
        SysUser user = sysUserMapper.findByUsername(existing.getStudentNumber()).orElse(null);
        if (user != null) {
            user.setRealName(dto.getName());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setDeptId(dto.getDeptId());
            sysUserMapper.updateById(user);
        }
    }

    /**
     * 禁用/启用毕业生账号
     */
    @Transactional
    public void toggleStatus(Long id) {
        Graduate graduate = graduateMapper.selectById(id);
        if (graduate == null) {
            throw new BusinessException("毕业生不存在");
        }
        int newStatus = graduate.getGraduateStatus() == 1 ? 0 : 1;
        graduate.setGraduateStatus(newStatus);
        graduateMapper.updateById(graduate);

        // 同步禁用/启用用户账号
        SysUser user = sysUserMapper.findByUsername(graduate.getStudentNumber()).orElse(null);
        if (user != null) {
            sysUserMapper.updateStatus(user.getId(), newStatus);
        }
    }

    /**
     * 获取毕业生详情
     */
    public GraduateVO getById(Long id) {
        Graduate graduate = graduateMapper.selectById(id);
        if (graduate == null) {
            throw new BusinessException("毕业生不存在");
        }
        // 教师数据隔离：仅本院系
        checkDeptPermission(graduate);

        Map<Long, String> deptMap = departmentMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        Map<Long, Major> majorMap = majorMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Major::getId, m -> m));
        Map<Long, ClassInfo> classMap = classInfoMapper.selectAllActive().stream()
                .collect(Collectors.toMap(ClassInfo::getId, c -> c));

        GraduateVO vo = new GraduateVO();
        BeanUtils.copyProperties(graduate, vo);
        vo.setDeptName(deptMap.getOrDefault(graduate.getDeptId(), ""));
        Major major = majorMap.get(graduate.getMajorId());
        vo.setMajorName(major != null ? major.getName() : "");
        ClassInfo cls = classMap.get(graduate.getClassId());
        vo.setClassName(cls != null ? cls.getName() : "");
        return vo;
    }

    /**
     * 学生获取本人基本信息（自动绑定当前登录学号，无需前端传递身份参数）
     * 权限：仅 STUDENT
     */
    public GraduateVO getMyProfile() {
        String studentNumber = securityUtil.getStudentNumber();
        if (studentNumber == null) {
            throw new BusinessException(403, "仅学生可访问本人信息");
        }
        Graduate graduate = graduateMapper.selectOne(
                new LambdaQueryWrapper<Graduate>().eq(Graduate::getStudentNumber, studentNumber));
        if (graduate == null) {
            throw new BusinessException(404, "未找到本人的毕业生档案，请联系管理员");
        }

        Map<Long, String> deptMap = departmentMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        Map<Long, Major> majorMap = majorMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Major::getId, m -> m));
        Map<Long, ClassInfo> classMap = classInfoMapper.selectAllActive().stream()
                .collect(Collectors.toMap(ClassInfo::getId, c -> c));

        GraduateVO vo = new GraduateVO();
        BeanUtils.copyProperties(graduate, vo);
        vo.setDeptName(deptMap.getOrDefault(graduate.getDeptId(), ""));
        Major major = majorMap.get(graduate.getMajorId());
        vo.setMajorName(major != null ? major.getName() : "");
        ClassInfo cls = classMap.get(graduate.getClassId());
        vo.setClassName(cls != null ? cls.getName() : "");
        return vo;
    }

    /**
     * 学生更新本人基本信息
     * 仅允许修改非关键字段（手机号、邮箱、性别）；学号、姓名、院系、专业、班级为只读
     * 权限：仅 STUDENT，自动绑定当前登录学号，忽略前端提交的关键字段
     */
    @Transactional
    public void updateMyProfile(com.college.employment.application.dto.StudentProfileUpdateDTO dto) {
        String studentNumber = securityUtil.getStudentNumber();
        if (studentNumber == null) {
            throw new BusinessException(403, "仅学生可修改本人信息");
        }
        Graduate graduate = graduateMapper.selectOne(
                new LambdaQueryWrapper<Graduate>().eq(Graduate::getStudentNumber, studentNumber));
        if (graduate == null) {
            throw new BusinessException(404, "未找到本人的毕业生档案，请联系管理员");
        }

        // 非关键字段更新（关键字段只读，一律忽略前端提交）；性别为本人可修改项
        graduate.setPhone(dto.getPhone());
        graduate.setEmail(dto.getEmail());
        if (StringUtils.hasText(dto.getGender())) {
            graduate.setGender(dto.getGender());
        }
        graduateMapper.updateById(graduate);

        // 同步更新用户账号中的联系方式与性别
        SysUser user = sysUserMapper.findByUsername(studentNumber).orElse(null);
        if (user != null) {
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            if (StringUtils.hasText(dto.getGender())) {
                user.setGender(dto.getGender());
            }
            sysUserMapper.updateById(user);
        }
    }

    /**
     * 获取毕业生完整详情（基本信息 + 就业记录 + 审核进度）
     * 权限：ADMIN 可查看任意；TEACHER 仅本院系；STUDENT 仅本人
     */
    public GraduateDetailVO getDetail(Long id) {
        Graduate graduate = graduateMapper.selectById(id);
        if (graduate == null) {
            throw new BusinessException("毕业生不存在");
        }
        // 权限校验
        checkDetailPermission(graduate);

        // 基本信息
        GraduateVO basicInfo = getById(id);

        // 就业记录列表（按提交时间倒序）
        List<EmploymentRecordVO> records = employmentRecordMapper.selectList(
                        new LambdaQueryWrapper<EmploymentRecord>()
                                .eq(EmploymentRecord::getGraduateId, id)
                                .orderByDesc(EmploymentRecord::getCreateTime))
                .stream()
                .map(r -> {
                    EmploymentRecordVO vo = new EmploymentRecordVO();
                    BeanUtils.copyProperties(r, vo);
                    vo.setDestinationName(DESTINATION_NAMES.getOrDefault(r.getDestination(), r.getDestination()));
                    return vo;
                })
                .collect(Collectors.toList());

        // 最新一条申请的审核进度
        ReviewProgressVO progress = null;
        if (!records.isEmpty()) {
            EmploymentRecord latest = employmentRecordMapper.selectById(records.get(0).getId());
            progress = new ReviewProgressVO();
            progress.setReviewStatus(latest.getReviewStatus());
            progress.setReviewStatusName(REVIEW_STATUS_NAMES.getOrDefault(
                    latest.getReviewStatus(), latest.getReviewStatus()));
            progress.setStage(latest.getStage());
            progress.setStageName(STAGE_NAMES.getOrDefault(latest.getStage(), latest.getStage()));
            progress.setTeacherComment(latest.getTeacherComment());
            progress.setAdminComment(latest.getAdminComment());
            progress.setTeacherReviewTime(latest.getTeacherReviewTime());
            progress.setAdminReviewTime(latest.getAdminReviewTime());
            progress.setCreateTime(latest.getCreateTime());
        }

        GraduateDetailVO detail = new GraduateDetailVO();
        detail.setBasicInfo(basicInfo);
        detail.setEmploymentRecords(records);
        detail.setReviewProgress(progress);
        return detail;
    }

    /**
     * 分页查询某毕业生的就业申请历史记录
     * 权限：ADMIN 可查看任意；TEACHER 仅本院系；STUDENT 仅本人
     */
    public Page<EmploymentRecordVO> getEmploymentHistory(Long id, PageQueryDTO query) {
        Graduate graduate = graduateMapper.selectById(id);
        if (graduate == null) {
            throw new BusinessException("毕业生不存在");
        }
        // 权限校验（与详情一致）
        checkDetailPermission(graduate);

        Page<EmploymentRecord> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<EmploymentRecord> wrapper = new LambdaQueryWrapper<EmploymentRecord>()
                .eq(EmploymentRecord::getGraduateId, id)
                .orderByDesc(EmploymentRecord::getCreateTime);
        Page<EmploymentRecord> result = employmentRecordMapper.selectPage(page, wrapper);

        List<EmploymentRecordVO> voList = result.getRecords().stream().map(r -> {
            EmploymentRecordVO vo = new EmploymentRecordVO();
            BeanUtils.copyProperties(r, vo);
            vo.setDestinationName(DESTINATION_NAMES.getOrDefault(r.getDestination(), r.getDestination()));
            vo.setReviewStatusName(REVIEW_STATUS_NAMES.getOrDefault(r.getReviewStatus(), r.getReviewStatus()));
            vo.setStageName(STAGE_NAMES.getOrDefault(r.getStage(), r.getStage()));
            return vo;
        }).collect(Collectors.toList());

        Page<EmploymentRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 院系级权限校验：
     * ADMIN -> 任意；TEACHER -> 仅本院系；STUDENT -> 仅本人；其余角色禁止
     */
    private void checkDeptPermission(Graduate graduate) {
        if (securityUtil.isAdmin()) {
            return;
        }
        // 学生数据隔离：仅本人可查看
        String studentNumber = securityUtil.getStudentNumber();
        if (studentNumber != null) {
            if (!studentNumber.equals(graduate.getStudentNumber())) {
                throw new BusinessException(403, "无权查看他人数据");
            }
            return;
        }
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null) {
            if (!teacherDeptId.equals(graduate.getDeptId())) {
                throw new BusinessException(403, "无权查看其他院系毕业生数据");
            }
            return;
        }
        throw new BusinessException(403, "无权限执行此操作");
    }

    /**
     * 详情权限校验：
     * COLLEGE_ADMIN/SYSTEM_ADMIN -> 任意；TEACHER -> 本院系；GRADUATE -> 本人
     */
    private void checkDetailPermission(Graduate graduate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserDetails)) {
            throw new BusinessException(401, "未认证或登录已过期，请重新登录");
        }
        JwtUserDetails current = (JwtUserDetails) auth.getPrincipal();
        String role = current.getRole() == null ? "" : current.getRole().toUpperCase();

        if ("COLLEGE_ADMIN".equals(role) || "SYSTEM_ADMIN".equals(role)) {
            return; // 管理员可查看任意
        }
        if ("TEACHER".equals(role)) {
            // 教师仅可查看本院系毕业生
            SysUser user = sysUserMapper.findByUsername(current.getUsername()).orElse(null);
            Long teacherDeptId = user != null ? user.getDeptId() : null;
            if (teacherDeptId == null || !teacherDeptId.equals(graduate.getDeptId())) {
                throw new BusinessException(403, "无权查看其他院系毕业生详情");
            }
            return;
        }
        if ("GRADUATE".equals(role)) {
            // 学生仅可查看本人详情
            if (!current.getUsername().equals(graduate.getStudentNumber())) {
                throw new BusinessException(403, "无权查看他人详情");
            }
            return;
        }
        throw new BusinessException(403, "无权限执行此操作");
    }
}
