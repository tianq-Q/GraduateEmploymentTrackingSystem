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

package com.college.employment.application.track;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.college.employment.application.dto.track.TrackDetailVO;
import com.college.employment.application.dto.track.TrackSettingUpdateDTO;
import com.college.employment.application.dto.track.TrackSettingVO;
import com.college.employment.application.dto.track.TrackYearRecordVO;
import com.college.employment.common.exception.BusinessException;
import com.college.employment.common.util.SecurityUtil;
import com.college.employment.config.JwtUserDetails;
import com.college.employment.domain.model.EmploymentRecord;
import com.college.employment.domain.model.Graduate;
import com.college.employment.domain.model.SysUser;
import com.college.employment.domain.model.TrackSetting;
import com.college.employment.infrastructure.mapper.EmploymentRecordMapper;
import com.college.employment.infrastructure.mapper.GraduateMapper;
import com.college.employment.infrastructure.mapper.SysUserMapper;
import com.college.employment.infrastructure.mapper.TrackSettingMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 毕业生跟踪 应用服务
 *
 * <p>按毕业年份（届）配置跟踪年限（3年/5年），自动计算每个毕业生的跟踪进度，
 * 并基于 employment_record 就业记录按年度自动聚合就业状态变化（自动跟踪）。</p>
 */
@Service
@RequiredArgsConstructor
public class TrackAppService {

    /** 默认跟踪年限 */
    public static final int DEFAULT_TRACK_YEARS = 3;

    private final TrackSettingMapper trackSettingMapper;
    private final GraduateMapper graduateMapper;
    private final EmploymentRecordMapper employmentRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final SecurityUtil securityUtil;

    /** 去向类型字典：value -> label */
    private static final Map<String, String> DESTINATION_NAMES = Collections.unmodifiableMap(
            new HashMap<String, String>() {{
                put("SIGNED", "签约就业");
                put("FURTHER_STUDY", "升学深造");
                put("ABROAD", "出国留学");
                put("ENTREPRENEURSHIP", "自主创业");
                put("FLEXIBLE", "灵活就业");
                put("WAITING", "待就业");
            }});

    /**
     * 查询所有届的跟踪设置（含该届学生人数）
     */
    public List<TrackSettingVO> listSettings() {
        List<TrackSetting> settings = trackSettingMapper.selectList(
                new LambdaQueryWrapper<TrackSetting>().orderByDesc(TrackSetting::getGraduateYear));

        // 按毕业年份统计学生人数（map-underscore-to-camel-case 开启，key 为驼峰 graduateYear）
        Map<String, Long> yearCounts = graduateMapper.selectMaps(
                        new LambdaQueryWrapper<Graduate>()
                                .select(Graduate::getGraduateYear)
                                .isNotNull(Graduate::getGraduateYear)
                                .ne(Graduate::getGraduateYear, ""))
                .stream()
                .collect(Collectors.groupingBy(
                        m -> String.valueOf(m.get("graduateYear")),
                        Collectors.counting()));

        List<TrackSettingVO> voList = new ArrayList<>();
        for (TrackSetting s : settings) {
            TrackSettingVO vo = new TrackSettingVO();
            vo.setGraduateYear(s.getGraduateYear());
            vo.setTrackYears(s.getTrackYears() == null ? DEFAULT_TRACK_YEARS : s.getTrackYears());
            vo.setStudentCount(yearCounts.getOrDefault(s.getGraduateYear(), 0L));
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 新增/更新某届的跟踪年限
     */
    @Transactional
    public void updateSetting(TrackSettingUpdateDTO dto) {
        TrackSetting existing = trackSettingMapper.selectOne(
                new LambdaQueryWrapper<TrackSetting>().eq(TrackSetting::getGraduateYear, dto.getGraduateYear()));
        if (existing != null) {
            existing.setTrackYears(dto.getTrackYears());
            trackSettingMapper.updateById(existing);
            return;
        }
        TrackSetting setting = new TrackSetting();
        setting.setGraduateYear(dto.getGraduateYear());
        setting.setTrackYears(dto.getTrackYears());
        trackSettingMapper.insert(setting);
    }

    /**
     * 删除某届的跟踪设置（恢复默认 3 年）
     */
    @Transactional
    public void deleteSetting(String graduateYear) {
        trackSettingMapper.delete(new LambdaQueryWrapper<TrackSetting>()
                .eq(TrackSetting::getGraduateYear, graduateYear));
    }

    /**
     * 查询某毕业生的跟踪详情（进度 + 年度就业状态时间线）
     * 权限：ADMIN 任意；TEACHER 本院系；GRADUATE 本人
     */
    public TrackDetailVO getTrackDetail(Long graduateId) {
        Graduate graduate = graduateMapper.selectById(graduateId);
        if (graduate == null) {
            throw new BusinessException(404, "毕业生不存在");
        }
        checkDetailPermission(graduate);
        return buildTrackDetail(graduate);
    }

    /**
     * 学生查询本人的跟踪详情
     */
    public TrackDetailVO getMyTrack() {
        String studentNumber = securityUtil.getStudentNumber();
        if (studentNumber == null) {
            throw new BusinessException(403, "仅学生可查看本人的跟踪信息");
        }
        Graduate graduate = graduateMapper.selectOne(
                new LambdaQueryWrapper<Graduate>().eq(Graduate::getStudentNumber, studentNumber));
        if (graduate == null) {
            throw new BusinessException(404, "未找到本人的毕业生档案，请联系管理员");
        }
        return buildTrackDetail(graduate);
    }

    /**
     * 组装某毕业生的跟踪详情
     */
    private TrackDetailVO buildTrackDetail(Graduate graduate) {
        int currentYear = LocalDate.now().getYear();

        // 跟踪设置（无记录时用默认 3 年）
        TrackSetting setting = null;
        if (StringUtils.hasText(graduate.getGraduateYear())) {
            setting = trackSettingMapper.selectOne(new LambdaQueryWrapper<TrackSetting>()
                    .eq(TrackSetting::getGraduateYear, graduate.getGraduateYear()));
        }
        int trackYears = setting != null && setting.getTrackYears() != null ?
                setting.getTrackYears() : DEFAULT_TRACK_YEARS;

        TrackDetailVO vo = new TrackDetailVO();
        vo.setGraduateId(graduate.getId());
        vo.setStudentNumber(graduate.getStudentNumber());
        vo.setName(graduate.getName());
        vo.setGraduateYear(graduate.getGraduateYear());
        vo.setTrackYears(trackYears);

        int startYear;
        try {
            startYear = graduate.getGraduateYear() == null ? currentYear
                    : Integer.parseInt(graduate.getGraduateYear().trim());
        } catch (NumberFormatException e) {
            startYear = currentYear;
        }
        int endYear = startYear + trackYears - 1; // 毕业当年算第 1 年

        vo.setStartYear(startYear);
        vo.setEndYear(endYear);
        vo.setCurrentYear(currentYear);

        // 已跟踪年数（毕业当年 = 1），封顶到年限
        int trackedYears = Math.max(0, Math.min(currentYear - startYear + 1, trackYears));
        int remainingYears = Math.max(0, endYear - currentYear);
        vo.setTrackedYears(trackedYears);
        vo.setRemainingYears(remainingYears);

        boolean finished = currentYear > endYear;
        vo.setStatus(finished ? "FINISHED" : "TRACKING");
        vo.setStatusName(finished ? "跟踪已结束" : "跟踪中");
        vo.setProgress((double) trackedYears / trackYears);

        // 年度就业状态时间线（只统计审核通过的就业记录，按年取最新一条，倒序）
        vo.setYearRecords(buildYearRecords(graduate.getId()));
        return vo;
    }

    /**
     * 按年度聚合就业记录（审核通过），每年取最新一条，按年倒序
     */
    private List<TrackYearRecordVO> buildYearRecords(Long graduateId) {
        List<EmploymentRecord> records = employmentRecordMapper.selectList(
                new LambdaQueryWrapper<EmploymentRecord>()
                        .eq(EmploymentRecord::getGraduateId, graduateId)
                        .eq(EmploymentRecord::getReviewStatus, "APPROVED")
                        .orderByDesc(EmploymentRecord::getCreateTime));

        // 按年份分组，保留每年最新一条（因已倒序，groupingBy 保留第一条）
        Map<Integer, EmploymentRecord> latestByYear = new LinkedHashMap<>();
        for (EmploymentRecord r : records) {
            LocalDateTime t = r.getCreateTime();
            if (t == null) {
                continue;
            }
            int year = t.getYear();
            latestByYear.putIfAbsent(year, r);
        }

        return latestByYear.entrySet().stream()
                .sorted(Map.Entry.<Integer, EmploymentRecord>comparingByKey().reversed())
                .map(e -> {
                    EmploymentRecord r = e.getValue();
                    TrackYearRecordVO vo = new TrackYearRecordVO();
                    vo.setYear(e.getKey());
                    vo.setDestination(r.getDestination());
                    vo.setDestinationName(DESTINATION_NAMES.getOrDefault(r.getDestination(), r.getDestination()));
                    vo.setCompanyName(r.getCompanyName());
                    vo.setPosition(r.getPosition());
                    vo.setCity(r.getCity());
                    vo.setSalaryRange(r.getSalaryRange());
                    vo.setRecordTime(r.getCreateTime());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 详情权限校验：COLLEGE_ADMIN/SYSTEM_ADMIN 任意；TEACHER 本院系；GRADUATE 本人
     */
    private void checkDetailPermission(Graduate graduate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserDetails)) {
            throw new BusinessException(401, "未认证或登录已过期，请重新登录");
        }
        JwtUserDetails current = (JwtUserDetails) auth.getPrincipal();
        String role = current.getRole() == null ? "" : current.getRole().toUpperCase();

        if ("COLLEGE_ADMIN".equals(role) || "SYSTEM_ADMIN".equals(role)) {
            return;
        }
        if ("TEACHER".equals(role)) {
            SysUser user = sysUserMapper.findByUsername(current.getUsername()).orElse(null);
            Long teacherDeptId = user != null ? user.getDeptId() : null;
            if (teacherDeptId == null || !teacherDeptId.equals(graduate.getDeptId())) {
                throw new BusinessException(403, "无权查看其他院系毕业生的跟踪信息");
            }
            return;
        }
        if ("GRADUATE".equals(role)) {
            if (!current.getUsername().equals(graduate.getStudentNumber())) {
                throw new BusinessException(403, "无权查看他人的跟踪信息");
            }
            return;
        }
        throw new BusinessException(403, "无权限执行此操作");
    }
}
