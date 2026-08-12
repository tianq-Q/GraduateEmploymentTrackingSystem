package com.college.employment.controller;

import com.college.employment.common.api.Result;
import com.college.employment.common.constant.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 就业审核控制器 - 教师初审 + 校级/系统管理员终审
 */
@RestController
@RequestMapping("/api/teacher/review")
public class ReviewController {

    @GetMapping("/list")
    @PreAuthorize(RoleConstants.HAS_TEACHER_AND_ABOVE)
    public Result<?> list() {
        return Result.ok(null);
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize(RoleConstants.HAS_TEACHER_AND_ABOVE)
    public Result<?> approve(@PathVariable Long id) {
        return Result.ok(null);
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize(RoleConstants.HAS_TEACHER_AND_ABOVE)
    public Result<?> reject(@PathVariable Long id) {
        return Result.ok(null);
    }
}
