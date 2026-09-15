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

package com.college.employment.controller;

import com.college.employment.application.basedata.ClassAppService;
import com.college.employment.application.basedata.DepartmentAppService;
import com.college.employment.application.basedata.GraduateAppService;
import com.college.employment.application.basedata.MajorAppService;
import com.college.employment.application.basedata.TeacherAppService;
import com.college.employment.application.dto.basedata.ClassQueryDTO;
import com.college.employment.application.dto.basedata.ClassVO;
import com.college.employment.application.dto.basedata.DepartmentQueryDTO;
import com.college.employment.application.dto.basedata.DepartmentVO;
import com.college.employment.application.dto.basedata.GraduateQueryDTO;
import com.college.employment.application.dto.basedata.GraduateVO;
import com.college.employment.application.dto.basedata.MajorQueryDTO;
import com.college.employment.application.dto.basedata.MajorVO;
import com.college.employment.application.dto.basedata.TeacherQueryDTO;
import com.college.employment.application.dto.basedata.TeacherVO;
import com.college.employment.common.util.CsvExportUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用导出控制器。满足文档「二基本信息模块」2.3 导出按钮需求：
 * 导出院系/专业/班级/毕业生/教师基本信息报表（CSV，Excel 兼容）。
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Resource
    private DepartmentAppService departmentAppService;
    @Resource
    private MajorAppService majorAppService;
    @Resource
    private ClassAppService classAppService;
    @Resource
    private GraduateAppService graduateAppService;
    @Resource
    private TeacherAppService teacherAppService;

    private static final long MAX_SIZE = 100000L;

    /** 导出院系信息 CSV（支持关键字筛选） */
    @GetMapping("/departments")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public void exportDepartments(HttpServletResponse response,
                                  @RequestParam(required = false) String keyword) throws Exception {
        DepartmentQueryDTO q = new DepartmentQueryDTO();
        q.setKeyword(keyword);
        q.setPage(1L);
        q.setSize(MAX_SIZE);
        List<DepartmentVO> list = departmentAppService.listPage(q).getRecords();
        List<String> headers = new ArrayList<>();
        headers.add("编号");
        headers.add("院系名称");
        headers.add("院系代码");
        headers.add("简介");
        headers.add("专业数量");
        headers.add("毕业生数量");
        headers.add("创建时间");
        List<String> rows = new ArrayList<>();
        for (DepartmentVO vo : list) {
            rows.add(String.join(",",
                    CsvExportUtil.cell(vo.getId()),
                    CsvExportUtil.cell(vo.getName()),
                    CsvExportUtil.cell(vo.getCode()),
                    CsvExportUtil.cell(vo.getDescription()),
                    CsvExportUtil.cell(vo.getMajorCount()),
                    CsvExportUtil.cell(vo.getGraduateCount()),
                    CsvExportUtil.cell(vo.getCreateTime())));
        }
        CsvExportUtil.writeCsv(response, "院系信息", headers, rows);
    }

    /** 导出专业信息 CSV（支持关键字/院系筛选） */
    @GetMapping("/majors")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public void exportMajors(HttpServletResponse response,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) Long deptId) throws Exception {
        MajorQueryDTO q = new MajorQueryDTO();
        q.setKeyword(keyword);
        q.setDeptId(deptId);
        q.setPage(1L);
        q.setSize(MAX_SIZE);
        List<MajorVO> list = majorAppService.listPage(q).getRecords();
        List<String> headers = new ArrayList<>();
        headers.add("编号");
        headers.add("专业名称");
        headers.add("专业代码");
        headers.add("所属院系");
        headers.add("简介");
        headers.add("班级数量");
        headers.add("毕业生数量");
        headers.add("创建时间");
        List<String> rows = new ArrayList<>();
        for (MajorVO vo : list) {
            rows.add(String.join(",",
                    CsvExportUtil.cell(vo.getId()),
                    CsvExportUtil.cell(vo.getName()),
                    CsvExportUtil.cell(vo.getCode()),
                    CsvExportUtil.cell(vo.getDeptName()),
                    CsvExportUtil.cell(vo.getDescription()),
                    CsvExportUtil.cell(vo.getClassCount()),
                    CsvExportUtil.cell(vo.getGraduateCount()),
                    CsvExportUtil.cell(vo.getCreateTime())));
        }
        CsvExportUtil.writeCsv(response, "专业信息", headers, rows);
    }

    /** 导出班级信息 CSV（支持关键字/专业筛选） */
    @GetMapping("/classes")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public void exportClasses(HttpServletResponse response,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long majorId) throws Exception {
        ClassQueryDTO q = new ClassQueryDTO();
        q.setKeyword(keyword);
        q.setMajorId(majorId);
        q.setPage(1L);
        q.setSize(MAX_SIZE);
        List<ClassVO> list = classAppService.listPage(q).getRecords();
        List<String> headers = new ArrayList<>();
        headers.add("编号");
        headers.add("班级名称");
        headers.add("所属院系");
        headers.add("所属专业");
        headers.add("入学年份");
        headers.add("毕业年份");
        headers.add("简介");
        headers.add("毕业生数量");
        headers.add("创建时间");
        List<String> rows = new ArrayList<>();
        for (ClassVO vo : list) {
            rows.add(String.join(",",
                    CsvExportUtil.cell(vo.getId()),
                    CsvExportUtil.cell(vo.getName()),
                    CsvExportUtil.cell(vo.getDeptName()),
                    CsvExportUtil.cell(vo.getMajorName()),
                    CsvExportUtil.cell(vo.getEnrollmentYear()),
                    CsvExportUtil.cell(vo.getGraduationYear()),
                    CsvExportUtil.cell(vo.getDescription()),
                    CsvExportUtil.cell(vo.getGraduateCount()),
                    CsvExportUtil.cell(vo.getCreateTime())));
        }
        CsvExportUtil.writeCsv(response, "班级信息", headers, rows);
    }

    /** 导出毕业生信息 CSV（支持关键字/状态/班级/专业/院系筛选） */
    @GetMapping("/graduates")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public void exportGraduates(HttpServletResponse response,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) Long classId,
                                @RequestParam(required = false) Long majorId,
                                @RequestParam(required = false) Long deptId) throws Exception {
        GraduateQueryDTO q = new GraduateQueryDTO();
        q.setKeyword(keyword);
        q.setStatus(status);
        q.setClassId(classId);
        q.setMajorId(majorId);
        q.setDeptId(deptId);
        q.setPage(1L);
        q.setSize(MAX_SIZE);
        List<GraduateVO> list = graduateAppService.listPage(q).getRecords();
        List<String> headers = new ArrayList<>();
        headers.add("学号");
        headers.add("姓名");
        headers.add("性别");
        headers.add("院系");
        headers.add("专业");
        headers.add("班级");
        headers.add("手机号");
        headers.add("邮箱");
        headers.add("就业状态");
        headers.add("创建时间");
        List<String> rows = new ArrayList<>();
        for (GraduateVO vo : list) {
            rows.add(String.join(",",
                    CsvExportUtil.cell(vo.getStudentNumber()),
                    CsvExportUtil.cell(vo.getName()),
                    CsvExportUtil.cell(vo.getGender()),
                    CsvExportUtil.cell(vo.getDeptName()),
                    CsvExportUtil.cell(vo.getMajorName()),
                    CsvExportUtil.cell(vo.getClassName()),
                    CsvExportUtil.cell(vo.getPhone()),
                    CsvExportUtil.cell(vo.getEmail()),
                    CsvExportUtil.cell(vo.getStatus()),
                    CsvExportUtil.cell(vo.getCreateTime())));
        }
        CsvExportUtil.writeCsv(response, "毕业生信息", headers, rows);
    }

    /** 导出教师信息 CSV（支持关键字/院系筛选，仅管理员） */
    @GetMapping("/teachers")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public void exportTeachers(HttpServletResponse response,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long deptId) throws Exception {
        TeacherQueryDTO q = new TeacherQueryDTO();
        q.setKeyword(keyword);
        q.setDeptId(deptId);
        q.setPage(1L);
        q.setSize(MAX_SIZE);
        List<TeacherVO> list = teacherAppService.listPage(q).getRecords();
        List<String> headers = new ArrayList<>();
        headers.add("工号");
        headers.add("姓名");
        headers.add("所属院系");
        headers.add("手机号");
        headers.add("邮箱");
        headers.add("角色");
        headers.add("状态");
        List<String> rows = new ArrayList<>();
        for (TeacherVO vo : list) {
            rows.add(String.join(",",
                    CsvExportUtil.cell(vo.getJobNumber()),
                    CsvExportUtil.cell(vo.getName()),
                    CsvExportUtil.cell(vo.getDeptName()),
                    CsvExportUtil.cell(vo.getPhone()),
                    CsvExportUtil.cell(vo.getEmail()),
                    CsvExportUtil.cell(vo.getRole()),
                    CsvExportUtil.cell(vo.getStatus())));
        }
        CsvExportUtil.writeCsv(response, "教师信息", headers, rows);
    }
}
