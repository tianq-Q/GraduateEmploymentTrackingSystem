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
import com.college.employment.domain.model.Department;
import com.college.employment.domain.model.Graduate;
import com.college.employment.infrastructure.mapper.DepartmentMapper;
import com.college.employment.infrastructure.mapper.GraduateMapper;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 院系管理 应用服务
 */
@Service
@RequiredArgsConstructor
public class DepartmentAppService {

    private final DepartmentMapper departmentMapper;
    private final GraduateMapper graduateMapper;
    private final SecurityUtil securityUtil;

    /**
     * 分页查询院系列表
     */
    public Page<DepartmentVO> listPage(DepartmentQueryDTO query) {
        Page<Department> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();

        // 教师数据隔离：仅返回本院系
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null) {
            wrapper.eq(Department::getId, teacherDeptId);
        }

        String keyword = query.getKeyword();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Department::getName, keyword)
                    .or().like(Department::getCode, keyword));
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Department::getName, query.getName());
        }
        if (StringUtils.hasText(query.getCode())) {
            wrapper.like(Department::getCode, query.getCode());
        }
        wrapper.orderByAsc(Department::getSortOrder).orderByAsc(Department::getId);

        Page<Department> result = departmentMapper.selectPage(page, wrapper);
        List<DepartmentVO> voList = result.getRecords().stream().map(dept -> {
            DepartmentVO vo = new DepartmentVO();
            BeanUtils.copyProperties(dept, vo);
            vo.setMajorCount((long) departmentMapper.countMajorsByDeptId(dept.getId()));
            vo.setGraduateCount((long) departmentMapper.countGraduatesByDeptId(dept.getId()));
            return vo;
        }).collect(Collectors.toList());

        Page<DepartmentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 查询所有院系（下拉列表用）
     */
    public List<DepartmentSimpleVO> listAll() {
        // 教师数据隔离：下拉仅返回本院系
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        List<Department> depts;
        if (teacherDeptId != null) {
            depts = departmentMapper.selectAllActive().stream()
                    .filter(d -> teacherDeptId.equals(d.getId()))
                    .collect(Collectors.toList());
        } else {
            depts = departmentMapper.selectAllActive();
        }
        return depts.stream().map(dept -> {
            DepartmentSimpleVO vo = new DepartmentSimpleVO();
            vo.setId(dept.getId());
            vo.setName(dept.getName());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 新增院系
     */
    @Transactional
    public void create(DepartmentCreateDTO dto) {
        if (departmentMapper.countByCode(dto.getCode()) > 0) {
            throw new BusinessException("院系编码已存在");
        }
        Department dept = new Department();
        BeanUtils.copyProperties(dto, dept);
        dept.setStatus(1);
        if (dto.getSortOrder() == null) {
            dept.setSortOrder(0);
        }
        departmentMapper.insert(dept);
    }

    /**
     * 更新院系
     */
    @Transactional
    public void update(DepartmentUpdateDTO dto) {
        Department existing = departmentMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("院系不存在");
        }
        // 检查编码唯一性（排除自身）
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getCode, dto.getCode()).ne(Department::getId, dto.getId());
        if (departmentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("院系编码已存在");
        }
        BeanUtils.copyProperties(dto, existing);
        departmentMapper.updateById(existing);
    }

    /**
     * 删除院系（软删除，检查级联）
     */
    @Transactional
    public void delete(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("院系不存在");
        }
        if (departmentMapper.countMajorsByDeptId(id) > 0) {
            throw new BusinessException("该院系下存在专业，无法删除");
        }
        if (departmentMapper.countGraduatesByDeptId(id) > 0) {
            throw new BusinessException("该院系下存在毕业生，无法删除");
        }
        departmentMapper.deleteById(id);
    }

    /**
     * 院系统计
     */
    public DepartmentStatisticsVO getStatistics(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("院系不存在");
        }
        // 教师数据隔离：仅可统计本院系
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null && !teacherDeptId.equals(id)) {
            throw new BusinessException(403, "无权查看其他院系数据");
        }

        LambdaQueryWrapper<Graduate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Graduate::getDeptId, id);

        DepartmentStatisticsVO vo = new DepartmentStatisticsVO();
        vo.setDeptId(id);
        vo.setDeptName(dept.getName());

        List<Graduate> graduates = graduateMapper.selectList(wrapper);
        vo.setTotalGraduates((long) graduates.size());

        Map<String, Long> statusMap = graduates.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getStatus() == null ? "待就业" : g.getStatus(), Collectors.counting()));

        vo.setEmployedCount(statusMap.getOrDefault("已签约", 0L));
        vo.setUnemployedCount(statusMap.getOrDefault("待就业", 0L));
        vo.setFurtherStudyCount(statusMap.getOrDefault("升学", 0L));
        vo.setEntrepreneurshipCount(statusMap.getOrDefault("创业", 0L));
        vo.setAbroadCount(statusMap.getOrDefault("出国", 0L));

        if (vo.getTotalGraduates() > 0) {
            long employed = graduates.stream().filter(g ->
                    "已签约".equals(g.getStatus()) || "升学".equals(g.getStatus()) ||
                            "创业".equals(g.getStatus()) || "出国".equals(g.getStatus())
            ).count();
            vo.setEmploymentRate(String.format("%.1f%%", employed * 100.0 / vo.getTotalGraduates()));
        } else {
            vo.setEmploymentRate("0.0%");
        }
        return vo;
    }

    /**
     * 获取院系详情
     */
    public DepartmentVO getById(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("院系不存在");
        }
        // 教师数据隔离：仅可查看本院系
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null && !teacherDeptId.equals(id)) {
            throw new BusinessException(403, "无权查看其他院系数据");
        }
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(dept, vo);
        vo.setMajorCount((long) departmentMapper.countMajorsByDeptId(id));
        vo.setGraduateCount((long) departmentMapper.countGraduatesByDeptId(id));
        return vo;
    }
}
