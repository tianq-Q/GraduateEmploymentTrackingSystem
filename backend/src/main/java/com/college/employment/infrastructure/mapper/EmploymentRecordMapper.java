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
import com.college.employment.application.dto.DepartmentCompareVO;
import com.college.employment.application.dto.DestinationChartVO;
import com.college.employment.domain.model.EmploymentRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmploymentRecordMapper extends BaseMapper<EmploymentRecord> {

    @Select("SELECT id, graduate_id, company_name, company_type, industry, position, salary_range, city," +
            " destination, review_status, review_comment, reviewer_id, is_proxy, submitter_id, submitter_name," +
            " evidence_url, stage, teacher_comment, admin_comment, teacher_review_time, admin_review_time," +
            " review_time, create_time, update_time FROM employment_record WHERE graduate_id = #{graduateId}" +
            " ORDER BY create_time DESC")
    List<EmploymentRecord> findByGraduateId(@Param("graduateId") Long graduateId);

    @Select("SELECT id, graduate_id, company_name, company_type, industry, position, salary_range, city," +
            " destination, review_status, review_comment, reviewer_id, is_proxy, submitter_id, submitter_name," +
            " evidence_url, stage, teacher_comment, admin_comment, teacher_review_time, admin_review_time," +
            " review_time, create_time, update_time FROM employment_record WHERE submitter_id = #{submitterId}" +
            " ORDER BY create_time DESC")
    List<EmploymentRecord> findBySubmitterId(@Param("submitterId") Long submitterId);

    @Select("SELECT id, graduate_id, company_name, company_type, industry, position, salary_range, city," +
            " destination, review_status, review_comment, reviewer_id, is_proxy, submitter_id, submitter_name," +
            " evidence_url, stage, teacher_comment, admin_comment, teacher_review_time, admin_review_time," +
            " review_time, create_time, update_time FROM employment_record WHERE graduate_id = #{graduateId}" +
            " ORDER BY create_time DESC LIMIT 1")
    EmploymentRecord findLatestByGraduateId(@Param("graduateId") Long graduateId);

    @Select("<script>" +
            "SELECT er.id, er.graduate_id, er.company_name, er.company_type, er.industry, er.position," +
            " er.salary_range, er.city, er.destination, er.review_status, er.review_comment, er.reviewer_id," +
            " er.is_proxy, er.submitter_id, er.submitter_name, er.evidence_url, er.stage, er.teacher_comment," +
            " er.admin_comment, er.teacher_review_time, er.admin_review_time, er.review_time, er.create_time," +
            " er.update_time FROM employment_record er " +
            "LEFT JOIN graduate g ON er.graduate_id = g.id " +
            "WHERE 1=1 " +
            "<if test='studentNo != null and studentNo != \"\"'> AND g.student_no LIKE CONCAT('%', #{studentNo}," +
            " '%') </if>" +
            "<if test='name != null and name != \"\"'> AND g.name LIKE CONCAT('%', #{name}, '%') </if>" +
            "<if test='companyName != null and companyName != \"\"'> AND er.company_name LIKE CONCAT('%'," +
            " #{companyName}, '%') </if>" +
            "<if test='destination != null and destination != \"\"'> AND er.destination = #{destination} </if>" +
            "<if test='reviewStatus != null and reviewStatus != \"\"'> AND er.review_status = #{reviewStatus} </if>" +
            "<if test='deptId != null'> AND (g.dept_id = #{deptId} OR g.dept_id IS NULL) </if>" +
            "ORDER BY er.create_time DESC " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<EmploymentRecord> findPage(@Param("studentNo") String studentNo,
                                  @Param("name") String name,
                                  @Param("companyName") String companyName,
                                  @Param("destination") String destination,
                                  @Param("reviewStatus") String reviewStatus,
                                  @Param("deptId") Long deptId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM employment_record er " +
            "LEFT JOIN graduate g ON er.graduate_id = g.id " +
            "WHERE 1=1 " +
            "<if test='studentNo != null and studentNo != \"\"'> AND g.student_no LIKE CONCAT('%', #{studentNo}," +
            " '%') </if>" +
            "<if test='name != null and name != \"\"'> AND g.name LIKE CONCAT('%', #{name}, '%') </if>" +
            "<if test='companyName != null and companyName != \"\"'> AND er.company_name LIKE CONCAT('%'," +
            " #{companyName}, '%') </if>" +
            "<if test='destination != null and destination != \"\"'> AND er.destination = #{destination} </if>" +
            "<if test='reviewStatus != null and reviewStatus != \"\"'> AND er.review_status = #{reviewStatus} </if>" +
            "<if test='deptId != null'> AND (g.dept_id = #{deptId} OR g.dept_id IS NULL) </if>" +
            "</script>")
    long countPage(@Param("studentNo") String studentNo,
                 @Param("name") String name,
                 @Param("companyName") String companyName,
                 @Param("destination") String destination,
                 @Param("reviewStatus") String reviewStatus,
                 @Param("deptId") Long deptId);

    @Select("<script>" +
            "SELECT er.id, er.graduate_id, er.company_name, er.company_type, er.industry, er.position," +
            " er.salary_range, er.city, er.destination, er.review_status, er.review_comment, er.reviewer_id," +
            " er.is_proxy, er.submitter_id, er.submitter_name, er.evidence_url, er.stage, er.teacher_comment," +
            " er.admin_comment, er.teacher_review_time, er.admin_review_time, er.review_time, er.create_time," +
            " er.update_time FROM employment_record er " +
            "LEFT JOIN graduate g ON er.graduate_id = g.id " +
            "WHERE er.review_status = #{reviewStatus} " +
            "<if test='deptId != null'> AND (g.dept_id = #{deptId} OR g.dept_id IS NULL) </if>" +
            "<if test='name != null and name != \"\"'> AND g.name LIKE CONCAT('%', #{name}, '%') </if>" +
            "<if test='companyName != null and companyName != \"\"'> AND er.company_name LIKE CONCAT('%'," +
            " #{companyName}, '%') </if>" +
            "<if test='destination != null and destination != \"\"'> AND er.destination = #{destination} </if>" +
            "<if test='startTime != null'> AND er.create_time &gt;= #{startTime} </if>" +
            "<if test='endTime != null'> AND er.create_time &lt;= #{endTime} </if>" +
            "ORDER BY er.create_time ASC" +
            "</script>")
    List<EmploymentRecord> findByReviewStatus(@Param("reviewStatus") String reviewStatus,
                                             @Param("deptId") Long deptId,
                                             @Param("name") String name,
                                             @Param("companyName") String companyName,
                                             @Param("destination") String destination,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    // ============================================================
    // 数据看板统计（SQL 见 resources/mapper/EmploymentRecordMapper.xml）
    // ============================================================

    /** 看板概览统计：毕业生总数 / 已落实 / 升学 / 创业 */
    Map<String, Object> selectOverview(@Param("deptIds") List<Long> deptIds);

    /** 院系就业率对比 */
    List<DepartmentCompareVO> selectDepartmentCompare(@Param("deptIds") List<Long> deptIds);

    /** 就业去向分布 */
    List<DestinationChartVO> selectDestinationDistribution(@Param("deptIds") List<Long> deptIds);

    /** 行业分布 Top N */
    List<Map<String, Object>> selectIndustryDistribution(@Param("deptIds") List<Long> deptIds,
                                                       @Param("limit") int limit);

    /** 单位性质分布 */
    List<Map<String, Object>> selectCompanyTypeDistribution(@Param("deptIds") List<Long> deptIds);

    /** 月度就业率趋势（按终审通过月份累计） */
    List<Map<String, Object>> selectMonthlyTrend(@Param("deptIds") List<Long> deptIds);
}
