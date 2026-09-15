/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.employment.domain.model.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知 数据访问接口
 */
@Mapper
/** 通知数据访问接口（touch） */
public interface NotificationMapper extends BaseMapper<Notification> {
}
