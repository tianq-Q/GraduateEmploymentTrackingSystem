/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto;

import lombok.Data;

/**
 * 操作日志查询条件（touch）
 */
@Data
public class OperationLogQueryDTO {
    /** 模块筛选（如 dashboard / employment） */
    private String module;

    /** 操作人姓名关键字 */
    private String operatorName;

    /** 起始时间 yyyy-MM-dd */
    private String startDate;

    /** 结束时间 yyyy-MM-dd */
    private String endDate;
}
