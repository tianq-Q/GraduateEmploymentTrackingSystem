package com.college.employment.controller;

import com.college.employment.common.api.Result;
import com.college.employment.common.constant.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 就业登记控制器 - 毕业生提交就业信息
 */
@RestController
@RequestMapping("/api/student/employment")
@PreAuthorize(RoleConstants.HAS_GRADUATE)
public class EmploymentController {

    @PostMapping("/submit")
    public Result<?> submit() {
        return Result.ok(null);
    }

    @GetMapping("/status")
    public Result<?> status() {
        return Result.ok(null);
    }
}
