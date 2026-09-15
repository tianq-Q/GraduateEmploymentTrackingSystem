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
import com.college.employment.domain.model.SysUser;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT id, username, password, real_name, gender, student_number, phone, email, avatar, role," +
            " dept_id, status, create_time, update_time FROM sys_user WHERE student_number = #{studentNumber} AND" +
            " status = 1 AND deleted = 0")
    Optional<SysUser> findByStudentNumber(@Param("studentNumber") String studentNumber);

    @Select("SELECT COUNT(*) FROM sys_user WHERE student_number = #{studentNumber} AND deleted = 0")
    int countByStudentNumber(@Param("studentNumber") String studentNumber);

    @Select("SELECT id, username, real_name, student_number, phone, email, role, dept_id, status FROM sys_user" +
            " WHERE role = #{role} AND status = 1 AND deleted = 0")
    List<SysUser> findByRole(@Param("role") String role);

    @Select("SELECT id, username, real_name, student_number, phone, email, role, dept_id, status FROM sys_user" +
            " WHERE role = #{role} AND dept_id = #{deptId} AND status = 1 AND deleted = 0")
    List<SysUser> findByRoleAndDept(@Param("role") String role, @Param("deptId") Long deptId);

    @Select("SELECT id, username, password, real_name, gender, student_number, phone, email, avatar, role," +
            " dept_id, status, create_time, update_time, deleted FROM sys_user WHERE username = #{username} AND" +
            " deleted = 0")
    Optional<SysUser> findByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND deleted = 0")
    int countByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND id != #{id} AND deleted = 0")
    int countByUsernameExcludeSelf(@Param("username") String username, @Param("id") Long id);

    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
