/*
 * Copyright (c) 2026 EmploymentTracking Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.college.employment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.employment.domain.model.Department;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 院系 Mapper
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT COUNT(*) FROM major WHERE dept_id = #{deptId} AND deleted = 0")
    int countMajorsByDeptId(Long deptId);

    @Select("SELECT COUNT(*) FROM graduate WHERE dept_id = #{deptId} AND deleted = 0")
    int countGraduatesByDeptId(Long deptId);

    @Select("SELECT COUNT(*) FROM sys_user WHERE dept_id = #{deptId} AND deleted = 0")
    int countTeachersByDeptId(Long deptId);

    @Select("SELECT COUNT(*) FROM department WHERE code = #{code} AND deleted = 0")
    int countByCode(String code);

    @Select("SELECT DISTINCT d.id, d.name, d.code, d.sort_order, d.status, d.create_time, d.update_time," +
            " d.deleted, d.description FROM department d WHERE d.deleted = 0 ORDER BY d.sort_order ASC, d.id ASC")
    List<Department> selectAllActive();
}
