package com.college.employment.controller;

import com.college.employment.common.api.Result;
import com.college.employment.common.constant.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 毕业生信息管理控制器 - 校级管理员及以上可管理
 */
@RestController
@RequestMapping("/api/admin/graduate")
@PreAuthorize(RoleConstants.HAS_COLLEGE_OR_SYSTEM)
public class GraduateController {

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(null);
    }
}
