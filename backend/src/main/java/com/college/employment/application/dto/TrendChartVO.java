/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 月度就业率趋势 VO（折线图，touch）
 */
@Data
public class TrendChartVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 月份 yyyy-MM */
    private String month;

    /** 累计就业率（百分比） */
    private Double rate;

    /** 当月新增落实人数 */
    private Long increment;
}
