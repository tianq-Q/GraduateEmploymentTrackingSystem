/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 看板概览统计 VO（数字卡片 + 就业率统计子功能核心数据，touch）
 */
@Data
public class DashboardOverviewVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 毕业生总数（基数） */
    private Long totalGraduates;

    /** 已落实去向人数（已就业 + 升学 + 创业 + 入伍，即终审通过记录数） */
    private Long employedCount;

    /** 待就业人数 */
    private Long waitingCount;

    /** 升学深造人数 */
    private Long furtherStudyCount;

    /** 自主创业人数 */
    private Long entrepreneurshipCount;

    /** 总体就业率（已落实 / 总数，百分比） */
    private Double employmentRate;

    /** 截至当前同步时间 */
    private String syncTime;
}
