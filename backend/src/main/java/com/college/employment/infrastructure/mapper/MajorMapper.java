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
import com.college.employment.domain.model.Major;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 专业 Mapper
 */
@Mapper
public interface MajorMapper extends BaseMapper<Major> {

    @Select("SELECT COUNT(*) FROM class_info WHERE major_id = #{majorId} AND deleted = 0")
    int countClassesByMajorId(Long majorId);

    @Select("SELECT COUNT(*) FROM graduate WHERE major_id = #{majorId} AND deleted = 0")
    int countGraduatesByMajorId(Long majorId);

    @Select("SELECT COUNT(*) FROM major WHERE code = #{code} AND deleted = 0")
    int countByCode(String code);

    @Select("SELECT m.id, m.name, m.code, m.dept_id, m.sort_order, m.status, m.create_time, m.update_time," +
            " m.deleted, m.description FROM major m WHERE m.dept_id = #{deptId} AND m.deleted = 0 ORDER BY" +
            " m.sort_order ASC, m.id ASC")
    List<Major> selectByDeptId(Long deptId);

    @Select("SELECT m.id, m.name, m.code, m.dept_id, m.sort_order, m.status, m.create_time, m.update_time," +
            " m.deleted, m.description FROM major m WHERE m.deleted = 0 ORDER BY m.sort_order ASC, m.id ASC")
    List<Major> selectAllActive();
}
