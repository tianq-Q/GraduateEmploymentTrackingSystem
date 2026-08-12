package com.college.employment.controller;

import com.college.employment.application.AuthAppService;
import com.college.employment.application.dto.LoginRequest;
import com.college.employment.application.dto.LoginResponse;
import com.college.employment.application.dto.RegisterRequest;
import com.college.employment.common.api.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthAppService authAppService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authAppService.login(request);
        return Result.ok("登录成功", response);
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authAppService.register(request);
        return Result.ok("注册成功", response);
    }
}
