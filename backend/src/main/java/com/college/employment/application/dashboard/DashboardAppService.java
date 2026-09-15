/*
 * Copyright (c) 2026 employment-tracking. All rights reserved.
 */
package com.college.employment.application.dashboard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.employment.application.dto.dashboard.NameValueVO;
import com.college.employment.application.dto.dashboard.TeacherDashboardStatVO;
import com.college.employment.application.dto.DashboardOverviewVO;
import com.college.employment.application.dto.DashboardSyncResultVO;
import com.college.employment.application.dto.DepartmentCompareVO;
import com.college.employment.application.dto.DestinationChartVO;
import com.college.employment.application.dto.IndustryChartVO;
import com.college.employment.application.dto.OperationLogQueryDTO;
import com.college.employment.application.dto.TrendChartVO;
import com.college.employment.common.exception.BusinessException;
import com.college.employment.common.util.SecurityUtil;
import com.college.employment.domain.model.Department;
import com.college.employment.domain.model.EmploymentRecord;
import com.college.employment.domain.model.Graduate;
import com.college.employment.domain.model.Notification;
import com.college.employment.domain.model.OperationLog;
import com.college.employment.infrastructure.mapper.DepartmentMapper;
import com.college.employment.infrastructure.mapper.EmploymentRecordMapper;
import com.college.employment.infrastructure.mapper.GraduateMapper;
import com.college.employment.infrastructure.mapper.NotificationMapper;
import com.college.employment.infrastructure.mapper.OperationLogMapper;
import com.college.employment.infrastructure.security.CurrentUserContext;
import com.college.employment.infrastructure.security.DataScopeHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * 数据看板应用服务
 *
 * <p>职责边界（严格遵守需求约束）：
 * <ul>
 *   <li>① 就业率统计：聚合 employment_record 中 review_status = 'APPROVED'（终审通过）的记录；</li>
 *   <li>② 可视化图表：派生院系对比/去向分布/行业分布/单位性质/月度趋势数据；</li>
 *   <li>③ 导出：生成 CSV 报表（后端），前端再二次加工；</li>
 *   <li>④ 业务通知：读取 notification 表，支持未读统计与按用户过滤；</li>
 *   <li>⑤ 操作日志：分页查询 operation_log，并自行记录看板同步/导出动作。</li>
 * </ul>
 *
 * <p><b>重要约束：本服务对所有统计均为只读聚合，绝不调用任何业务表的写操作，
 * 不会反向修改毕业生/就业记录原始数据。</b></p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAppService {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 就业状态展示顺序（graduate.employment_status 使用中文取值，与前端字典一致） */
    private static final List<String> EMPLOYMENT_STATUS_ORDER = List.of(
            "已签约", "升学", "创业", "出国", "灵活就业", "待就业", "暂不就业");

    /** 视为"待就业"的状态（其余视为已就业） */
    private static final List<String> UNEMPLOYED_STATUSES = List.of("待就业", "暂不就业");

    private final EmploymentRecordMapper employmentRecordMapper;
    private final GraduateMapper graduateMapper;
    private final NotificationMapper notificationMapper;
    private final OperationLogMapper operationLogMapper;
    private final DataScopeHelper dataScopeHelper;
    private final CurrentUserContext currentUserContext;
    private final DepartmentMapper departmentMapper;
    private final SecurityUtil securityUtil;

    // ============================================================
    // ① 就业率统计
    // ============================================================

    /**
     * 看板概览统计（数字卡片）
     */
    public DashboardOverviewVO overview() {
        List<Long> deptIds = dataScopeHelper.resolveDeptIds();
        Map<String, Object> map = employmentRecordMapper.selectOverview(deptIds);

        long total = longVal(map.get("totalGraduates"));
        long employed = longVal(map.get("employedCount"));
        long further = longVal(map.get("furtherStudyCount"));
        long entr = longVal(map.get("entrepreneurshipCount"));
        long waiting = Math.max(total - employed, 0);

        DashboardOverviewVO vo = new DashboardOverviewVO();
        vo.setTotalGraduates(total);
        vo.setEmployedCount(employed);
        vo.setWaitingCount(waiting);
        vo.setFurtherStudyCount(further);
        vo.setEntrepreneurshipCount(entr);
        vo.setEmploymentRate(percent(employed, total));
        vo.setSyncTime(LocalDateTime.now().format(DTF));
        return vo;
    }

    /**
     * 教师端本院系就业总览统计（基础信息模块支撑接口）。
     *
     * <p>仅教师（TEACHER）可访问，自动按当前登录教师所属院系隔离；
     * 所有指标实时聚合，不修改任何业务数据。</p>
     *
     * <p>graduate.employment_status 使用中文取值
     * （已签约/升学/创业/出国/灵活就业/待就业/暂不就业），
     * 待就业 = 待就业/暂不就业，其余为已就业。</p>
     */
    public TeacherDashboardStatVO getTeacherStat() {
        String role = securityUtil.getRole();
        if (!SecurityUtil.ROLE_TEACHER.equals(role)) {
            throw new BusinessException(403, "仅教师可访问本院系看板");
        }
        Long deptId = securityUtil.getTeacherDeptId();
        if (deptId == null) {
            throw new BusinessException(403, "当前教师未分配院系，无法查看本院系看板");
        }
        Department dept = departmentMapper.selectById(deptId);

        // 本院系毕业生（实时统计）
        List<Graduate> graduates = graduateMapper.selectList(
                new LambdaQueryWrapper<Graduate>().eq(Graduate::getDeptId, deptId));
        long total = graduates.size();

        // 就业状态分布（中文取值 -> 数量，按声明顺序输出；空值归为"未填报"）
        Map<String, Long> statusCount = graduates.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getStatus() == null || g.getStatus().isBlank() ? "UNKNOWN" : g.getStatus(),
                        Collectors.counting()));
        List<NameValueVO> distribution = new ArrayList<>();
        for (String status : EMPLOYMENT_STATUS_ORDER) {
            Long count = statusCount.getOrDefault(status, 0L);
            if (count > 0) {
                distribution.add(new NameValueVO(status, count));
            }
        }
        Long unknown = statusCount.get("UNKNOWN");
        if (unknown != null && unknown > 0) {
            distribution.add(new NameValueVO("未填报", unknown));
        }

        // 已就业 / 待就业 / 就业率（未填报不计入已就业，也不计入待就业基数外展）
        long employed = statusCount.entrySet().stream()
                .filter(e -> !"UNKNOWN".equals(e.getKey()))
                .filter(e -> !UNEMPLOYED_STATUSES.contains(e.getKey()))
                .mapToLong(Map.Entry::getValue)
                .sum();
        long unemployed = total - employed;
        double rate = total == 0 ? 0.0 : Math.round(employed * 1000.0 / total) / 10.0;

        // 待审核就业申请数（本院系毕业生提交、尚待审核的记录）
        long pending = 0;
        if (!graduates.isEmpty()) {
            List<Long> graduateIds = graduates.stream()
                    .map(Graduate::getId)
                    .collect(Collectors.toList());
            pending = employmentRecordMapper.selectCount(
                    new LambdaQueryWrapper<EmploymentRecord>()
                            .in(EmploymentRecord::getGraduateId, graduateIds)
                            .eq(EmploymentRecord::getReviewStatus, "PENDING"));
        }

        TeacherDashboardStatVO vo = new TeacherDashboardStatVO();
        vo.setDeptId(deptId);
        vo.setDeptName(dept == null ? null : dept.getName());
        vo.setTotalGraduates(total);
        vo.setEmployedCount(employed);
        vo.setUnemployedCount(unemployed);
        vo.setEmploymentRate(rate);
        vo.setPendingReviews(pending);
        vo.setStatusDistribution(distribution);
        return vo;
    }

    // ============================================================
    // ② 可视化图表
    // ============================================================

    /** 院系就业率对比（柱状图） */
    public List<DepartmentCompareVO> departmentCompare() {
        return employmentRecordMapper.selectDepartmentCompare(dataScopeHelper.resolveDeptIds());
    }

    /** 就业去向分布（饼图） */
    public List<DestinationChartVO> destinationDistribution() {
        return employmentRecordMapper.selectDestinationDistribution(dataScopeHelper.resolveDeptIds());
    }

    /** 行业分布（柱状图） */
    public List<IndustryChartVO> industryDistribution(int limit) {
        List<Map<String, Object>> rows = employmentRecordMapper.selectIndustryDistribution(
                dataScopeHelper.resolveDeptIds(), limit);
        return toIndustryVO(rows);
    }

    /** 单位性质分布（柱状图/饼图） */
    public List<IndustryChartVO> companyTypeDistribution() {
        List<Map<String, Object>> rows = employmentRecordMapper.selectCompanyTypeDistribution(
                dataScopeHelper.resolveDeptIds());
        return toIndustryVO(rows);
    }

    /** 月度就业率趋势（折线图） */
    public List<TrendChartVO> monthlyTrend() {
        List<Long> deptIds = dataScopeHelper.resolveDeptIds();
        long total = graduateMapper.selectCount(null); // 基数仅用于参考率，不在权限过滤内波动展示
        List<Map<String, Object>> rows = employmentRecordMapper.selectMonthlyTrend(deptIds);

        // 累计口径：每月累计落实人数 / 毕业生总数
        List<TrendChartVO> result = new ArrayList<>();
        long acc = 0L;
        for (Map<String, Object> row : rows) {
            String month = String.valueOf(row.get("month"));
            long inc = longVal(row.get("increment"));
            acc += inc;
            TrendChartVO vo = new TrendChartVO();
            vo.setMonth(month);
            vo.setIncrement(inc);
            vo.setRate(percent(acc, total));
            result.add(vo);
        }
        return result;
    }

    // ============================================================
    // 数据同步（刷新统计视图）
    // ============================================================

    /**
     * 触发看板数据同步刷新。
     * 看板采用实时聚合策略，刷新动作即重新聚合源数据（终审通过的记录），
     * 并记录一条操作日志。
     *
     * @return 同步结果
     */
    public DashboardSyncResultVO refresh() {
        long startTime = System.currentTimeMillis();
        // 重新聚合一次，校验数据源可访问
        DashboardOverviewVO snapshot = overview();
        long synced = snapshot.getEmployedCount();
        long cost = System.currentTimeMillis() - startTime;

        DashboardSyncResultVO result = new DashboardSyncResultVO();
        result.setSuccess(true);
        result.setSyncedCount(synced);
        result.setSyncTime(LocalDateTime.now().format(DTF));
        result.setMessage("看板统计视图已刷新，聚合耗时 " + cost + "ms");

        // 记录操作日志
        writeOperationLog("dashboard", "SYNC",
                "刷新看板统计数据，聚合终审通过记录 " + synced + " 条");
        return result;
    }

    // ============================================================
    // ③ 报表导出（后端生成 CSV 内容）
    // ============================================================

    /**
     * 导出看板统计数据为 CSV 文本。
     * 返回 CSV 字符串，由 Controller 以附件形式返回；仅导出当前用户数据权限范围内的统计结果。
     */
    public String exportCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("统计项,数值\n");
        DashboardOverviewVO o = overview();
        sb.append("毕业生总数,").append(o.getTotalGraduates()).append("\n");
        sb.append("已落实去向人数,").append(o.getEmployedCount()).append("\n");
        sb.append("待就业人数,").append(o.getWaitingCount()).append("\n");
        sb.append("升学深造人数,").append(o.getFurtherStudyCount()).append("\n");
        sb.append("自主创业人数,").append(o.getEntrepreneurshipCount()).append("\n");
        sb.append("总体就业率(%),").append(o.getEmploymentRate()).append("\n");
        sb.append("\n院系,毕业生数,已落实,就业率(%)\n");
        for (DepartmentCompareVO d : departmentCompare()) {
            sb.append(d.getDeptName()).append(",")
                    .append(d.getTotal()).append(",")
                    .append(d.getEmployed()).append(",")
                    .append(d.getRate()).append("\n");
        }
        sb.append("\n去向类型,人数\n");
        for (DestinationChartVO d : destinationDistribution()) {
            sb.append(d.getDestination()).append(",").append(d.getCount()).append("\n");
        }

        writeOperationLog("dashboard", "EXPORT", "导出看板统计数据报表(CSV)");
        return sb.toString();
    }

    // ============================================================
    // ④ 业务通知
    // ============================================================

    /**
     * 当前用户的通知列表
     */
    public List<Notification> notifications(boolean onlyUnread) {
        Long userId = currentUserContext.getUserId();
        if (userId == null) {
            return Collections.emptyList();
        }
        return notificationMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<Notification>lambdaQuery()
                        .eq(Notification::getUserId, userId)
                        .eq(onlyUnread, Notification::getIsRead, 0)
                        .orderByDesc(Notification::getCreateTime)
        );
    }

    /**
     * 当前用户未读通知数量
     */
    public long unreadNotificationCount() {
        Long userId = currentUserContext.getUserId();
        if (userId == null) {
            return 0;
        }
        return notificationMapper.selectCount(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<Notification>lambdaQuery()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)
        );
    }

    /**
     * 标记通知为已读
     */
    public void markNotificationRead(Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n == null) {
            throw new BusinessException(404, "通知不存在");
        }
        n.setIsRead(1);
        notificationMapper.updateById(n);
    }

    // ============================================================
    // ⑤ 操作日志
    // ============================================================

    /**
     * 分页查询操作日志（看板"操作日志"子功能）
     */
    public IPage<OperationLog> operationLogs(int page, int size, OperationLogQueryDTO query) {
        Page<OperationLog> p = new Page<>(page, size);
        return operationLogMapper.selectPage(p,
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<OperationLog>lambdaQuery()
                        .eq(query.getModule() != null, OperationLog::getModule, query.getModule())
                        .like(query.getOperatorName() != null, OperationLog::getOperatorName, query.getOperatorName())
                        .orderByDesc(OperationLog::getCreateTime)
        );
    }

    // ------------------------------------------------------------
    // 内部工具
    // ------------------------------------------------------------

    private void writeOperationLog(String module, String action, String desc) {
        try {
            OperationLog log = new OperationLog();
            log.setModule(module);
            log.setAction(action);
            log.setDescription(desc);
            log.setOperatorId(currentUserContext.getUserId());
            log.setOperatorName(currentUserContext.getUsername());
            log.setIp(""); // 实际可由 RequestContext 注入，此处留空
            operationLogMapper.insert(log);
        } catch (DataAccessException e) {
            log.warn("Failed to write operation log (non-fatal): {}", e.getMessage());
        }
    }

    private List<IndustryChartVO> toIndustryVO(List<Map<String, Object>> rows) {
        if (rows == null) {
            return Collections.emptyList();
        }
        return rows.stream().map(r -> {
            IndustryChartVO vo = new IndustryChartVO();
            vo.setName(String.valueOf(r.get("name")));
            vo.setCount(longVal(r.get("count")));
            return vo;
        }).collect(Collectors.toList());
    }

    private long longVal(Object o) {
        if (o == null) {
            return 0L;
        }
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        try {
            return Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * 计算百分比，避免浮点误差。
     */
    private double percent(long part, long total) {
        if (total <= 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(part)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
