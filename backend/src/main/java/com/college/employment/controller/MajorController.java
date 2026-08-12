package com.college.employment.controller;

import com.college.employment.common.constant.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 专业管理控制器 - 校级管理员及以上
 */
@RestController
@RequestMapping("/api/admin/majors")
@PreAuthorize(RoleConstants.HAS_COLLEGE_OR_SYSTEM)
public class MajorController {
}
