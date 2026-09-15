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

package com.college.employment.controller;

import com.college.employment.application.dto.track.TrackDetailVO;
import com.college.employment.application.dto.track.TrackSettingUpdateDTO;
import com.college.employment.application.dto.track.TrackSettingVO;
import com.college.employment.application.track.TrackAppService;
import com.college.employment.common.api.Result;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 毕业生跟踪 Controller
 */
@RestController
@RequestMapping("/api/track")
@RequiredArgsConstructor
public class TrackController {

    private final TrackAppService trackAppService;

    /**
     * 查询所有届的跟踪设置（含该届学生人数）
     */
    @GetMapping("/settings")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','SYSTEM_ADMIN')")
    public Result<List<TrackSettingVO>> listSettings() {
        return Result.ok(trackAppService.listSettings());
    }

    /**
     * 新增/更新某届的跟踪年限（3/5 年）
     */
    @PutMapping("/settings")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','SYSTEM_ADMIN')")
    public Result<Void> updateSetting(@Valid @RequestBody TrackSettingUpdateDTO dto) {
        trackAppService.updateSetting(dto);
        return Result.ok();
    }

    /**
     * 删除某届的跟踪设置（恢复默认 3 年）
     */
    @DeleteMapping("/settings/{graduateYear}")
    @PreAuthorize("hasAnyRole('COLLEGE_ADMIN','SYSTEM_ADMIN')")
    public Result<Void> deleteSetting(@PathVariable String graduateYear) {
        trackAppService.deleteSetting(graduateYear);
        return Result.ok();
    }

    /**
     * 查询某毕业生的跟踪详情（进度 + 年度就业状态时间线）
     * 权限：ADMIN 任意；TEACHER 本院系；GRADUATE 本人（方法内校验）
     */
    @GetMapping("/graduate/{id}")
    public Result<TrackDetailVO> getTrackDetail(@PathVariable Long id) {
        return Result.ok(trackAppService.getTrackDetail(id));
    }

    /**
     * 学生查询本人的跟踪详情
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('GRADUATE','STUDENT')")
    public Result<TrackDetailVO> getMyTrack() {
        return Result.ok(trackAppService.getMyTrack());
    }
}
