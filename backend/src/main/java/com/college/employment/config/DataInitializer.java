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

package com.college.employment.config;

import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动初始化器：在数据库无对应账号时创建四个内置账号，便于首次登录。
 *
 * <p>仅在账号不存在时插入（幂等），已存在则跳过，不覆盖密码。
 * 内置账号：
 * <ul>
 *   <li>admin / admin123 —— 系统管理员</li>
 *   <li>C20230001 / 123456 —— 主任（校级管理员）</li>
 *   <li>T20230001 / 123456 —— 教师</li>
 *   <li>20230001 / 123456 —— 毕业生</li>
 * </ul>
 * 生产环境部署后建议立即修改默认密码。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    /** Spring Boot 启动完成后依次初始化各角色内置账号 */
    @Override
    public void run(String... args) {
        initSystemAdmin();
        initCollegeAdmin();
        initTeacher();
        initGraduate();
    }

    /** 初始化系统管理员（admin / admin123） */
    private void initSystemAdmin() {
        if (!sysUserMapper.findByStudentNumber("admin").isPresent()) {
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setStudentNumber("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRealName("System Admin");
            user.setRole("SYSTEM_ADMIN");
            user.setStatus(1);
            user.setPhone("13800000000");
            sysUserMapper.insert(user);
            log.info("SYSTEM_ADMIN created: admin / admin123");
        }
    }

    /** 初始化主任账号（C20230001 / 123456） */
    private void initCollegeAdmin() {
        if (!sysUserMapper.findByStudentNumber("C20230001").isPresent()) {
            SysUser user = new SysUser();
            user.setUsername("C20230001");
            user.setStudentNumber("C20230001");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRealName("Director Wang");
            user.setRole("COLLEGE_ADMIN");
            user.setDeptId(1L);
            user.setStatus(1);
            sysUserMapper.insert(user);
            log.info("COLLEGE_ADMIN created: C20230001 / 123456");
        }
    }

    /** 初始化教师账号（T20230001 / 123456） */
    private void initTeacher() {
        if (!sysUserMapper.findByStudentNumber("T20230001").isPresent()) {
            SysUser user = new SysUser();
            user.setUsername("T20230001");
            user.setStudentNumber("T20230001");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRealName("Teacher Zhang");
            user.setRole("TEACHER");
            user.setDeptId(1L);
            user.setStatus(1);
            sysUserMapper.insert(user);
            log.info("TEACHER created: T20230001 / 123456");
        }
    }

    /** 初始化毕业生账号（20230001 / 123456） */
    private void initGraduate() {
        if (!sysUserMapper.findByStudentNumber("20230001").isPresent()) {
            SysUser user = new SysUser();
            user.setUsername("20230001");
            user.setStudentNumber("20230001");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRealName("Student Li");
            user.setRole("GRADUATE");
            user.setDeptId(1L);
            user.setStatus(1);
            sysUserMapper.insert(user);
            log.info("GRADUATE created: 20230001 / 123456");
        }
    }
}
