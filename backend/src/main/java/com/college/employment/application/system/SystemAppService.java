/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.employment.application.dto.system.UserPageQueryDTO;
import com.college.employment.application.dto.system.UserVO;
import com.college.employment.common.exception.BusinessException;
import com.college.employment.common.util.SecurityUtil;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.Department;
import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.DepartmentMapper;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统管理应用服务（系统管理员专用）
 *
 * <p>职责：主任 / 教师 / 毕业生三类账号的查询与启用/禁用。
 * 系统管理员账号本身不参与管理，避免误操作锁死系统。</p>
 */
@Service
@RequiredArgsConstructor
public class SystemAppService {

    private static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";

    private final SysUserMapper sysUserMapper;
    private final DepartmentMapper departmentMapper;
    private final SecurityUtil securityUtil;

    /**
     * 分页查询账号（排除系统管理员自身，不展示不可操作）
     */
    public Page<UserVO> pageUsers(UserPageQueryDTO query) {
        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("deleted = 0");
        wrapper.ne(SysUser::getRole, ROLE_SYSTEM_ADMIN);
        if (StringUtils.hasText(query.getRole())) {
            wrapper.eq(SysUser::getRole, query.getRole());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(SysUser::getUsername, kw)
                    .or().like(SysUser::getRealName, kw)
                    .or().like(SysUser::getStudentNumber, kw));
        }
        if (StringUtils.hasText(query.getDeptName())) {
            // 院系名称模糊检索：先按名称查院系 id，再按 dept_id 过滤
            List<Long> deptIds = departmentMapper.selectList(
                            new LambdaQueryWrapper<Department>()
                                    .like(Department::getName, query.getDeptName().trim())
                                    .select(Department::getId))
                    .stream().map(Department::getId).collect(Collectors.toList());
            if (deptIds.isEmpty()) {
                Page<UserVO> empty = new Page<>(page.getCurrent(), page.getSize(), 0);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            wrapper.in(SysUser::getDeptId, deptIds);
        }
        wrapper.orderByDesc(SysUser::getId);
        sysUserMapper.selectPage(page, wrapper);

        Map<Long, String> deptMap = loadDeptMap(page.getRecords());
        List<UserVO> vos = page.getRecords().stream()
                .map(u -> toVO(u, deptMap))
                .collect(Collectors.toList());
        Page<UserVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(vos);
        return result;
    }

    /**
     * 启用/禁用账号
     *
     * @param status 1=启用 0=禁用
     */
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态参数非法，仅支持 0(禁用) 或 1(启用)");
        }
        SysUser target = sysUserMapper.selectById(id);
        if (target == null) {
            throw new BusinessException("账号不存在");
        }
        if (ROLE_SYSTEM_ADMIN.equals(target.getRole())) {
            throw new BusinessException("系统管理员账号不可操作");
        }
        JwtUserDetails current = securityUtil.requireLogin();
        if (current.getUserId() != null && current.getUserId().equals(id)) {
            throw new BusinessException("不能修改当前登录账号的状态");
        }
        sysUserMapper.updateStatus(id, status);
    }

    private Map<Long, String> loadDeptMap(List<SysUser> users) {
        List<Long> ids = users.stream()
                .map(SysUser::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return departmentMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Department::getId, Department::getName, (a, b) -> a));
    }

    private UserVO toVO(SysUser u, Map<Long, String> deptMap) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(u, vo);
        vo.setRoleName(roleName(u.getRole()));
        if (u.getDeptId() != null) {
            vo.setDeptName(deptMap.get(u.getDeptId()));
        }
        return vo;
    }

    private String roleName(String role) {
        if (role == null) {
            return "";
        }
        switch (role) {
            case "COLLEGE_ADMIN":
                return "主任";
            case "TEACHER":
                return "教师";
            case "GRADUATE":
                return "毕业生";
            case "SYSTEM_ADMIN":
                return "系统管理员";
            default:
                return role;
        }
    }
}
