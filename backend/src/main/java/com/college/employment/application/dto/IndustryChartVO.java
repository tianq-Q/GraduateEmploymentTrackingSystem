/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 行业 / 单位性质 分布 VO（柱状图，touch）
 */
@Data
public class IndustryChartVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Long count;
}
