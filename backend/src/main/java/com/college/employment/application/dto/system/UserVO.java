/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto.system;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 账号管理视图对象（系统管理员 - 主任/教师/毕业生账号）
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String studentNumber;
    private String role;
    private String roleName;
    private Long deptId;
    private String deptName;
    private Integer status;
    private LocalDateTime createTime;
}
