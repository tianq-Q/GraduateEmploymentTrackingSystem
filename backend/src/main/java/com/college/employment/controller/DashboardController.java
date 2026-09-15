/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.college.employment.application.dashboard.DashboardAppService;
import com.college.employment.application.dto.dashboard.TeacherDashboardStatVO;
import com.college.employment.application.dto.DashboardOverviewVO;
import com.college.employment.application.dto.DashboardSyncResultVO;
import com.college.employment.application.dto.DepartmentCompareVO;
import com.college.employment.application.dto.DestinationChartVO;
import com.college.employment.application.dto.IndustryChartVO;
import com.college.employment.application.dto.OperationLogQueryDTO;
import com.college.employment.application.dto.TrendChartVO;
import com.college.employment.common.api.PageResult;
import com.college.employment.common.api.Result;
import com.college.employment.domain.model.Notification;
import com.college.employment.domain.model.OperationLog;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板模块 REST 控制器
 *
 * 路由前缀 /api/dashboard。
 * 所有统计接口均为只读，不修改任何业务数据。
 * 权限：COLLEGE_ADMIN/SYSTEM_ADMIN 可见全量；TEACHER 经 DataScopeHelper 按院系隔离。
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardAppService dashboardAppService;

    // ===================== ① 就业率统计 =====================

    @GetMapping("/overview")
    public Result<DashboardOverviewVO> overview() {
        return Result.ok(dashboardAppService.overview());
    }

    /** 教师端本院系就业总览统计（仅教师可访问，Service 内校验） */
    @GetMapping("/teacher/stat")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public Result<TeacherDashboardStatVO> teacherStat() {
        return Result.ok(dashboardAppService.getTeacherStat());
    }

    // ===================== ② 可视化图表 =====================

    @GetMapping("/charts/department")
    public Result<List<DepartmentCompareVO>> departmentCompare() {
        return Result.ok(dashboardAppService.departmentCompare());
    }

    @GetMapping("/charts/destination")
    public Result<List<DestinationChartVO>> destinationDistribution() {
        return Result.ok(dashboardAppService.destinationDistribution());
    }

    @GetMapping("/charts/industry")
    public Result<List<IndustryChartVO>> industryDistribution(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.ok(dashboardAppService.industryDistribution(limit));
    }

    @GetMapping("/charts/company-type")
    public Result<List<IndustryChartVO>> companyTypeDistribution() {
        return Result.ok(dashboardAppService.companyTypeDistribution());
    }

    @GetMapping("/charts/trend")
    public Result<List<TrendChartVO>> monthlyTrend() {
        return Result.ok(dashboardAppService.monthlyTrend());
    }

    // ===================== 数据同步 =====================

    @PostMapping("/sync")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public Result<DashboardSyncResultVO> sync() {
        return Result.ok(dashboardAppService.refresh());
    }

    // ===================== ③ 报表导出 =====================

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','TEACHER')")
    public void exportCsv(HttpServletResponse response) throws Exception {
        String csv = dashboardAppService.exportCsv();
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("就业看板统计报表.csv", "UTF-8"));
        // 解决 Excel 打开中文乱码：写入 BOM
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);
        response.getOutputStream().write(csv.getBytes(StandardCharsets.UTF_8));
        response.getOutputStream().flush();
    }

    // ===================== ④ 业务通知 =====================

    @GetMapping("/notifications")
    public Result<List<Notification>> notifications(
            @RequestParam(defaultValue = "false") boolean onlyUnread) {
        return Result.ok(dashboardAppService.notifications(onlyUnread));
    }

    @GetMapping("/notifications/unread-count")
    public Result<Long> unreadCount() {
        return Result.ok(dashboardAppService.unreadNotificationCount());
    }

    @PostMapping("/notifications/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        dashboardAppService.markNotificationRead(id);
        return Result.ok(null);
    }

    // ===================== ⑤ 操作日志 =====================

    @GetMapping("/logs")
    public Result<PageResult<OperationLog>> logs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            OperationLogQueryDTO query) {
        IPage<OperationLog> p = dashboardAppService.operationLogs(page, size, query);
        return Result.ok(PageResult.of(p.getTotal(), page, size, p.getRecords()));
    }
}
