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
import com.college.employment.domain.model.Major;
import com.college.employment.infrastructure.mapper.DepartmentMapper;
import com.college.employment.infrastructure.mapper.GraduateMapper;
import com.college.employment.infrastructure.mapper.MajorMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 专业管理 应用服务
 */
@Service
@RequiredArgsConstructor
public class MajorAppService {

    private final MajorMapper majorMapper;
    private final DepartmentMapper departmentMapper;
    private final GraduateMapper graduateMapper;
    private final SecurityUtil securityUtil;

    /**
     * 分页查询专业列表
     */
    public Page<MajorVO> listPage(MajorQueryDTO query) {
        Page<Major> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();

        // 教师数据隔离：仅返回本院系专业，强制覆盖前端传入的院系条件
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null) {
            query.setDeptId(teacherDeptId);
        }
        if (query.getDeptId() != null) {
            wrapper.eq(Major::getDeptId, query.getDeptId());
        }
        String keyword = query.getKeyword();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Major::getName, keyword)
                    .or().like(Major::getCode, keyword));
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Major::getName, query.getName());
        }
        if (StringUtils.hasText(query.getCode())) {
            wrapper.like(Major::getCode, query.getCode());
        }
        wrapper.orderByAsc(Major::getSortOrder).orderByAsc(Major::getId);

        Page<Major> result = majorMapper.selectPage(page, wrapper);

        // 获取所有院系映射
        List<Department> depts = departmentMapper.selectAllActive();
        Map<Long, String> deptMap = depts.stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));

        List<MajorVO> voList = result.getRecords().stream().map(major -> {
            MajorVO vo = new MajorVO();
            BeanUtils.copyProperties(major, vo);
            vo.setDeptName(deptMap.getOrDefault(major.getDeptId(), ""));
            vo.setClassCount((long) majorMapper.countClassesByMajorId(major.getId()));
            vo.setGraduateCount((long) majorMapper.countGraduatesByMajorId(major.getId()));
            return vo;
        }).collect(Collectors.toList());

        Page<MajorVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 查询某院系下的所有专业（下拉用）
     */
    public List<MajorSimpleVO> listByDeptId(Long deptId) {
        // 教师数据隔离：仅返回本院系专业，强制覆盖传入的院系条件
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null) {
            deptId = teacherDeptId;
        }
        List<Major> majors;
        if (deptId != null) {
            majors = majorMapper.selectByDeptId(deptId);
        } else {
            majors = majorMapper.selectAllActive();
        }

        Map<Long, String> deptMap = departmentMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));

        return majors.stream().map(major -> {
            MajorSimpleVO vo = new MajorSimpleVO();
            vo.setId(major.getId());
            vo.setName(major.getName());
            vo.setDeptId(major.getDeptId());
            vo.setDeptName(deptMap.getOrDefault(major.getDeptId(), ""));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 新增专业
     */
    @Transactional
    public void create(MajorCreateDTO dto) {
        Department dept = departmentMapper.selectById(dto.getDeptId());
        if (dept == null) {
            throw new BusinessException("所属院系不存在");
        }
        if (majorMapper.countByCode(dto.getCode()) > 0) {
            throw new BusinessException("专业编码已存在");
        }
        Major major = new Major();
        BeanUtils.copyProperties(dto, major);
        major.setStatus(1);
        if (dto.getSortOrder() == null) {
            major.setSortOrder(0);
        }
        majorMapper.insert(major);
    }

    /**
     * 更新专业
     */
    @Transactional
    public void update(MajorUpdateDTO dto) {
        Major existing = majorMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("专业不存在");
        }
        Department dept = departmentMapper.selectById(dto.getDeptId());
        if (dept == null) {
            throw new BusinessException("所属院系不存在");
        }
        // 编码唯一性检查
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Major::getCode, dto.getCode()).ne(Major::getId, dto.getId());
        if (majorMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("专业编码已存在");
        }
        BeanUtils.copyProperties(dto, existing);
        majorMapper.updateById(existing);
    }

    /**
     * 删除专业
     */
    @Transactional
    public void delete(Long id) {
        Major major = majorMapper.selectById(id);
        if (major == null) {
            throw new BusinessException("专业不存在");
        }
        if (majorMapper.countClassesByMajorId(id) > 0) {
            throw new BusinessException("该专业下存在班级，无法删除");
        }
        if (majorMapper.countGraduatesByMajorId(id) > 0) {
            throw new BusinessException("该专业下存在毕业生，无法删除");
        }
        majorMapper.deleteById(id);
    }

    /**
     * 专业统计
     */
    public MajorStatisticsVO getStatistics(Long id) {
        Major major = majorMapper.selectById(id);
        if (major == null) {
            throw new BusinessException("专业不存在");
        }
        // 教师数据隔离：仅可统计本院系专业
        Long teacherDeptId = securityUtil.getTeacherDeptId();
        if (teacherDeptId != null && !teacherDeptId.equals(major.getDeptId())) {
            throw new BusinessException(403, "无权查看其他院系专业数据");
        }
        Department dept = departmentMapper.selectById(major.getDeptId());

        LambdaQueryWrapper<Graduate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Graduate::getMajorId, id);

        List<Graduate> graduates = graduateMapper.selectList(wrapper);

        MajorStatisticsVO vo = new MajorStatisticsVO();
        vo.setMajorId(id);
        vo.setMajorName(major.getName());
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
