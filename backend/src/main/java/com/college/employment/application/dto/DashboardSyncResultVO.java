/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 看板数据同步结果 VO
 */
@Data
public class DashboardSyncResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 是否成功 */
    private Boolean success;

    /** 同步记录条数（终审通过记录数） */
    private Long syncedCount;

    /** 同步完成时间 */
    private String syncTime;

    /** 提示信息 */
    private String message;
}
