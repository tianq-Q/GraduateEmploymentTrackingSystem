package com.college.employment.controller;

import com.college.employment.common.constant.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 附件上传控制器 - 所有角色可上传
 */
@RestController
@RequestMapping("/api/attachment")
@PreAuthorize(RoleConstants.HAS_ANY)
public class AttachmentController {
}
