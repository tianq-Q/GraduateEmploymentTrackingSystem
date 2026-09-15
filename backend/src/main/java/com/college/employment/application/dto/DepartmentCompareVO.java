/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 院系就业率对比 VO（柱状图）
 */
@Data
public class DepartmentCompareVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 院系ID */
    private Long deptId;

    /** 院系名称 */
    private String deptName;

    /** 毕业生总数 */
    private Long total;

    /** 已落实去向人数 */
    private Long employed;

    /** 就业率（百分比，保留2位） */
    private Double rate;
}
