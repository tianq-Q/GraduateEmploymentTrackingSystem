/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.employment.domain.model.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 数据访问接口
 */
@Mapper
/** 操作日志数据访问接口（touch） */
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
