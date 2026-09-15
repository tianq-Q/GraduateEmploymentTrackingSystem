/*
 * MIT License
 *
 * Copyright (c) 2026 Employment Tracking System
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.college.employment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.employment.domain.model.Graduate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GraduateMapper extends BaseMapper<Graduate> {

    String BASE_COLUMNS = "id, student_no, name, gender, id_card, phone, email, dept_id, major_id, class_id, " +
            "grade, graduate_year, employment_status, graduate_status, create_time, update_time";

    @Select("SELECT " + BASE_COLUMNS + " FROM graduate WHERE student_no = #{studentNo} AND deleted = 0")
    Graduate findByStudentNo(@Param("studentNo") String studentNo);

    @Select("SELECT " + BASE_COLUMNS + " FROM graduate WHERE name = #{name} AND deleted = 0 LIMIT 1")
    Graduate findByName(@Param("name") String name);

    @Select("<script>SELECT " + BASE_COLUMNS + " FROM graduate WHERE deleted = 0 AND id IN <foreach" +
            " collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Graduate> findByIds(@Param("ids") List<Long> ids);

    @Select("SELECT COUNT(*) FROM graduate WHERE student_no = #{studentNumber} AND deleted = 0")
    int countByStudentNumber(@Param("studentNumber") String studentNumber);

    @Select("SELECT COUNT(*) FROM graduate WHERE class_id = #{classId} AND deleted = 0")
    int countByClassId(@Param("classId") Long classId);

    @Select("SELECT COUNT(*) FROM graduate WHERE dept_id = #{deptId} AND deleted = 0")
    int countByDeptId(@Param("deptId") Long deptId);

    @Select("SELECT COUNT(*) FROM graduate WHERE major_id = #{majorId} AND deleted = 0")
    int countByMajorId(@Param("majorId") Long majorId);
}
