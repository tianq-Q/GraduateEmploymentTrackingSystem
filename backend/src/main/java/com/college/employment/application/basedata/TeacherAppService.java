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
import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.DepartmentMapper;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 教师账号管理 应用服务
 */
@Service
@RequiredArgsConstructor
public class TeacherAppService {

    private final SysUserMapper sysUserMapper;
    private final DepartmentMapper departmentMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询教师列表
     */
    public Page<TeacherVO> listPage(TeacherQueryDTO query) {
        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRole, SecurityUtil.ROLE_TEACHER);

        if (query.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, query.getDeptId());
        }
        String keyword = query.getKeyword();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword));
        }
        if (StringUtils.hasText(query.getJobNumber())) {
            wrapper.like(SysUser::getUsername, query.getJobNumber());
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(SysUser::getRealName, query.getName());
        }
        wrapper.orderByDesc(SysUser::getId);

        Page<SysUser> result = sysUserMapper.selectPage(page, wrapper);

        Map<Long, String> deptMap = departmentMapper.selectAllActive().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));

        List<TeacherVO> voList = result.getRecords().stream().map(user -> {
            TeacherVO vo = new TeacherVO();
            vo.setId(user.getId());
            vo.setJobNumber(user.getUsername());
            vo.setName(user.getRealName());
            vo.setDeptId(user.getDeptId());
            vo.setDeptName(deptMap.getOrDefault(user.getDeptId(), ""));
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setRole(user.getRole());
            vo.setStatus(user.getStatus());
            vo.setCreateTime(user.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        Page<TeacherVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 新增教师
     */
    @Transactional
    public void create(TeacherCreateDTO dto) {
        // 工号唯一性检查
        if (sysUserMapper.countByUsername(dto.getJobNumber()) > 0) {
            throw new BusinessException("工号已被使用");
        }

        Department dept = departmentMapper.selectById(dto.getDeptId());
        if (dept == null) {
            throw new BusinessException("所属院系不存在");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getJobNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(SecurityUtil.ROLE_TEACHER);
        user.setDeptId(dto.getDeptId());
        user.setStatus(1);
        user.setAvatar(null);
        sysUserMapper.insert(user);
    }

    /**
     * 更新教师信息
     */
    @Transactional
    public void update(TeacherUpdateDTO dto) {
        SysUser existing = sysUserMapper.selectById(dto.getId());
        if (existing == null || !SecurityUtil.ROLE_TEACHER.equalsIgnoreCase(existing.getRole())) {
            throw new BusinessException("教师不存在");
        }

        Department dept = departmentMapper.selectById(dto.getDeptId());
        if (dept == null) {
            throw new BusinessException("所属院系不存在");
        }

        existing.setRealName(dto.getName());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setDeptId(dto.getDeptId());
        sysUserMapper.updateById(existing);
    }

    /**
     * 禁用/启用教师账号
     */
    @Transactional
    public void toggleStatus(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || !SecurityUtil.ROLE_TEACHER.equalsIgnoreCase(user.getRole())) {
            throw new BusinessException("教师不存在");
        }
        int newStatus = user.getStatus() == 1 ? 0 : 1;
        sysUserMapper.updateStatus(id, newStatus);
    }
}
