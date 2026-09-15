/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto.system;

import lombok.Data;

/**
 * 账号分页查询条件
 */
@Data
public class UserPageQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    /** 角色筛选：COLLEGE_ADMIN / TEACHER / GRADUATE */
    private String role;
    /** 账号 / 姓名 / 学号 关键字（模糊检索） */
    private String keyword;
    /** 院系名称（模糊检索） */
    private String deptName;
    /** 账号状态：1=启用 0=禁用 */
    private Integer status;
}
