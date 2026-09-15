/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.domain.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 院系 领域实体
 */
@Data
@TableName("department")
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 院系名称 */
    private String name;

    /** 院系编码 */
    private String code;

    /** 排序号 */
    private Integer sortOrder;

    /** 状态 1启用 0禁用 */
    private Integer status;

    /** 院系简介 */
    private String description;

    /** 逻辑删除: 0正常 1删除 */
    @TableLogic
    private Integer deleted;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
}
