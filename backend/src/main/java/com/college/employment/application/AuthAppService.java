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

package com.college.employment.application;

import com.college.employment.application.dto.ChangePasswordDTO;
import com.college.employment.application.dto.LoginRequest;
import com.college.employment.application.dto.LoginResponse;
import com.college.employment.application.dto.RegisterRequest;
import com.college.employment.common.exception.BusinessException;
import com.college.employment.common.util.JwtUtil;
import com.college.employment.common.util.SecurityUtil;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.Department;
import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.DepartmentMapper;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证应用服务：登录、注册、修改密码。
 *
 * <p>登录/注册成功后统一签发 JWT 并组装 {@link LoginResponse} 返回前端；
 * 密码一律使用 BCrypt 存储与校验，不落明文。</p>
 */
@Service
@RequiredArgsConstructor
public class AuthAppService {

    private final SysUserMapper sysUserMapper;
    private final DepartmentMapper departmentMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;

    /** 登录：按学号/工号查账号，校验密码后签发 token */
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.findByStudentNumber(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "学号/工号或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "学号/工号或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getDeptId());

        return toLoginResponse(user, token);
    }

    /** 注册：校验学号唯一后创建毕业生账号，并直接签发 token 完成登录 */
    public LoginResponse register(RegisterRequest request) {
        if (sysUserMapper.countByStudentNumber(request.getStudentNumber()) > 0) {
            throw new BusinessException(400, "该学号/工号已被注册");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getStudentNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setStudentNumber(request.getStudentNumber());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole("GRADUATE");
        user.setStatus(1);

        sysUserMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getDeptId());

        return toLoginResponse(user, token);
    }

    /**
     * 修改当前登录用户密码（所有角色通用）
     */
    @Transactional
    public void changePassword(ChangePasswordDTO dto) {
        JwtUserDetails current = securityUtil.requireLogin();
        SysUser user = sysUserMapper.findByUsername(current.getUsername())
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new BusinessException(400, "新密码不能与原密码相同");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        sysUserMapper.updateById(user);
    }

    /** 组装登录响应：附带院系名称与用户基本信息 */
    private LoginResponse toLoginResponse(SysUser user, String token) {
        String deptName = null;
        if (user.getDeptId() != null) {
            Department dept = departmentMapper.selectById(user.getDeptId());
            if (dept != null) {
                deptName = dept.getName();
            }
        }
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole(),
                user.getAvatar(),
                user.getDeptId(),
                deptName,
                user.getGender()
        );
    }
}
