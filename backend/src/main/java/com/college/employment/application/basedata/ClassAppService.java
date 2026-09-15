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
import com.college.employment.domain.model.*;
import com.college.employment.infrastructure.mapper.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 班级管理 应用服务
 */
@Service
@RequiredArgsConstructor
public class ClassAppService {

    private final ClassInfoMapper classInfoMapper;
    private final MajorMapper majorMapper;
    private final DepartmentMapper departmentMapper;
    private final GraduateMapper graduateMapper;
    private final SecurityUtil securityUtil;

    /**
     * 分页查询班级列表
     */
    public Page<ClassVO> listPage(ClassQueryDTO query) {
        Page<ClassInfo> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<>();

        // 教师数据隔离：班级无院系字段，需通过所属专业关联院系过滤
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null) {
            List<Long> deptMajorIds = majorMapper.selectByDeptId(teacherDeptId).stream()
                    .map(Major::getId).collect(Collectors.toList());
            if (deptMajorIds.isEmpty()) {
                Page<ClassVO> empty = new Page<>(query.getPage(), query.getSize(), 0);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            wrapper.in(ClassInfo::getMajorId, deptMajorIds);
        }
        if (query.getMajorId() != null) {
            wrapper.eq(ClassInfo::getMajorId, query.getMajorId());
        }
        String keyword = query.getKeyword();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ClassInfo::getName, keyword);
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(ClassInfo::getName, query.getName());
        }
        if (query.getEnrollmentYear() != null) {
            wrapper.eq(ClassInfo::getEnrollmentYear, query.getEnrollmentYear());
        }
        if (query.getGraduationYear() != null) {
            wrapper.eq(ClassInfo::getGraduationYear, query.getGraduationYear());
        }
        wrapper.orderByAsc(ClassInfo::getSortOrder).orderByAsc(ClassInfo::getId);

        Page<ClassInfo> result = classInfoMapper.selectPage(page, wrapper);

        // 获取关联信息
        Map<Long, Major> majorMap = majorMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Major::getId, m -> m));
        Map<Long, String> deptMap = departmentMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));

        List<ClassVO> voList = result.getRecords().stream().map(cls -> {
            ClassVO vo = new ClassVO();
            BeanUtils.copyProperties(cls, vo);

            Major major = majorMap.get(cls.getMajorId());
            if (major != null) {
                vo.setMajorName(major.getName());
                vo.setDeptId(major.getDeptId());
                vo.setDeptName(deptMap.getOrDefault(major.getDeptId(), ""));
            }
            vo.setGraduateCount((long) classInfoMapper.countGraduatesByClassId(cls.getId()));
            return vo;
        }).collect(Collectors.toList());

        Page<ClassVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 查询某专业下的班级（下拉用）
     */
    public List<ClassSimpleVO> listByMajorId(Long majorId) {
        // 教师数据隔离：仅返回本院系班级，校验传入专业归属
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null) {
            if (majorId != null) {
                Major major = majorMapper.selectById(majorId);
                if (major == null || !teacherDeptId.equals(major.getDeptId())) {
                    return Collections.emptyList();
                }
            } else {
                List<Long> deptMajorIds = majorMapper.selectByDeptId(teacherDeptId).stream()
                        .map(Major::getId).collect(Collectors.toList());
                List<ClassInfo> deptClasses = deptMajorIds.isEmpty() ?
                        Collections.emptyList() :
                        classInfoMapper.selectList(new LambdaQueryWrapper<ClassInfo>()
                                .in(ClassInfo::getMajorId, deptMajorIds));
                return toSimpleVOList(deptClasses);
            }
        }
        List<ClassInfo> classes;
        if (majorId != null) {
            classes = classInfoMapper.selectByMajorId(majorId);
        } else {
            classes = classInfoMapper.selectAllActive();
        }
        return toSimpleVOList(classes);
    }

    private List<ClassSimpleVO> toSimpleVOList(List<ClassInfo> classes) {
        Map<Long, Major> majorMap = majorMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Major::getId, m -> m));

        return classes.stream().map(cls -> {
            ClassSimpleVO vo = new ClassSimpleVO();
            vo.setId(cls.getId());
            vo.setName(cls.getName());
            vo.setMajorId(cls.getMajorId());
            Major major = majorMap.get(cls.getMajorId());
            vo.setMajorName(major != null ? major.getName() : "");
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 新增班级
     */
    @Transactional
    public void create(ClassCreateDTO dto) {
        Major major = majorMapper.selectById(dto.getMajorId());
        if (major == null) {
            throw new BusinessException("所属专业不存在");
        }
        ClassInfo cls = new ClassInfo();
        BeanUtils.copyProperties(dto, cls);
        cls.setStatus(1);
        if (dto.getSortOrder() == null) {
            cls.setSortOrder(0);
        }
        // class_info 表 grade 为 NOT NULL 且无默认值，DTO 无 grade 字段，
        // 按数据语义（年级 = 入学年份）从 enrollmentYear 派生
        if (dto.getEnrollmentYear() != null) {
            cls.setGrade(String.valueOf(dto.getEnrollmentYear()));
        }
        classInfoMapper.insert(cls);
    }

    /**
     * 更新班级
     */
    @Transactional
    public void update(ClassUpdateDTO dto) {
        ClassInfo existing = classInfoMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("班级不存在");
        }
        Major major = majorMapper.selectById(dto.getMajorId());
        if (major == null) {
            throw new BusinessException("所属专业不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        classInfoMapper.updateById(existing);
    }

    /**
     * 删除班级
     */
    @Transactional
    public void delete(Long id) {
        ClassInfo cls = classInfoMapper.selectById(id);
        if (cls == null) {
            throw new BusinessException("班级不存在");
        }
        if (classInfoMapper.countGraduatesByClassId(id) > 0) {
            throw new BusinessException("该班级下存在毕业生，无法删除");
        }
        classInfoMapper.deleteById(id);
    }

    /**
     * 班级统计
     */
    public ClassStatisticsVO getStatistics(Long id) {
        ClassInfo cls = classInfoMapper.selectById(id);
        if (cls == null) {
            throw new BusinessException("班级不存在");
        }

        Major major = majorMapper.selectById(cls.getMajorId());
        // 教师数据隔离：仅可统计本院系班级
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null && (major == null || !teacherDeptId.equals(major.getDeptId()))) {
            throw new BusinessException(403, "无权查看其他院系班级数据");
        }
        Department dept = major != null ? departmentMapper.selectById(major.getDeptId()) : null;

        LambdaQueryWrapper<Graduate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Graduate::getClassId, id);

        List<Graduate> graduates = graduateMapper.selectList(wrapper);

        ClassStatisticsVO vo = new ClassStatisticsVO();
        vo.setClassId(id);
        vo.setClassName(cls.getName());
        vo.setMajorName(major != null ? major.getName() : "");
        vo.setDeptName(dept != null ? dept.getName() : "");
        vo.setTotalGraduates((long) graduates.size());

        long employed = graduates.stream().filter(g ->
                "已签约".equals(g.getStatus()) || "升学".equals(g.getStatus()) ||
                        "创业".equals(g.getStatus()) || "出国".equals(g.getStatus())
        ).count();
        vo.setEmployedCount(employed);
        vo.setUnemployedCount(graduates.size() - employed);

        if (vo.getTotalGraduates() > 0) {
            vo.setEmploymentRate(String.format("%.1f%%", employed * 100.0 / vo.getTotalGraduates()));
        } else {
            vo.setEmploymentRate("0.0%");
        }
        return vo;
    }
}
