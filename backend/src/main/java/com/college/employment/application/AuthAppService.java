package com.college.employment.application;

import com.college.employment.application.dto.LoginRequest;
import com.college.employment.application.dto.LoginResponse;
import com.college.employment.application.dto.RegisterRequest;
import com.college.employment.common.exception.BusinessException;
import com.college.employment.common.util.JwtUtil;
import com.college.employment.domain.model.SysUser;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthAppService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.findByStudentNumber(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "学号/工号或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "学号/工号或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getDeptId());

        return toLoginResponse(user, token);
    }

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

    private LoginResponse toLoginResponse(SysUser user, String token) {
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole(),
                user.getAvatar(),
                user.getDeptId()
        );
    }
}
