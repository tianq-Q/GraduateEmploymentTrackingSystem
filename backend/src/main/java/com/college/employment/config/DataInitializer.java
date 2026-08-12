package com.college.employment.config;

import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initSystemAdmin();
        initCollegeAdmin();
        initTeacher();
        initGraduate();
    }

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
