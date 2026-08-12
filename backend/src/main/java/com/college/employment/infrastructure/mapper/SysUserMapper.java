package com.college.employment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.employment.domain.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE student_number = #{studentNumber} AND status = 1")
    Optional<SysUser> findByStudentNumber(@Param("studentNumber") String studentNumber);

    @Select("SELECT COUNT(*) FROM sys_user WHERE student_number = #{studentNumber}")
    int countByStudentNumber(@Param("studentNumber") String studentNumber);
}
