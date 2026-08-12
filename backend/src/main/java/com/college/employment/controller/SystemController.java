package com.college.employment.controller;

import com.college.employment.common.constant.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 系统管理控制器 - 仅系统管理员（字典、配置、操作日志）
 */
@RestController
@RequestMapping("/api/admin/system")
@PreAuthorize(RoleConstants.HAS_SYSTEM_ADMIN)
public class SystemController {
}
