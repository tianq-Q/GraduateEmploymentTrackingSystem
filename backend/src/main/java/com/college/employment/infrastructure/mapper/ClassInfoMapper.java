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
import com.college.employment.domain.model.ClassInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 班级 Mapper
 */
@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    @Select("SELECT COUNT(*) FROM graduate WHERE class_id = #{classId} AND deleted = 0")
    int countGraduatesByClassId(Long classId);

    @Select("SELECT c.id, c.name, c.major_id, c.grade, c.sort_order, c.status, c.create_time, c.update_time," +
            " c.deleted, c.enrollment_year, c.graduation_year, c.description FROM class_info c WHERE c.major_id =" +
            " #{majorId} AND c.deleted = 0 ORDER BY c.sort_order ASC, c.id ASC")
    List<ClassInfo> selectByMajorId(Long majorId);

    @Select("SELECT c.id, c.name, c.major_id, c.grade, c.sort_order, c.status, c.create_time, c.update_time," +
            " c.deleted, c.enrollment_year, c.graduation_year, c.description FROM class_info c WHERE c.deleted =" +
            " 0 ORDER BY c.sort_order ASC, c.id ASC")
    List<ClassInfo> selectAllActive();
}
