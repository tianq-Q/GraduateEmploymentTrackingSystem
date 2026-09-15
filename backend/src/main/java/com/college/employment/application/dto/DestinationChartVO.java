/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 就业去向分布 VO（饼图）
 */
@Data
public class DestinationChartVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 去向类型 */
    private String destination;

    /** 人数 */
    private Long count;
}
