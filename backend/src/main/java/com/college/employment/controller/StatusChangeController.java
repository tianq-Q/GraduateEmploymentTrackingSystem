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

import com.college.employment.application.dto.track.StatusChangeSubmitDTO;
import com.college.employment.application.dto.track.StatusChangeVO;
import com.college.employment.application.track.StatusChangeAppService;
import com.college.employment.common.api.Result;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 就业状态流转 接口
 */
@RestController
@RequestMapping("/api/track/status-change")
@RequiredArgsConstructor
public class StatusChangeController {

    private final StatusChangeAppService statusChangeAppService;

    /**
     * 提交就业状态变更（学生本人/教师代录）
     */
    @PostMapping("/submit")
    public Result<StatusChangeVO> submit(@RequestBody StatusChangeSubmitDTO dto) {
        return Result.ok(statusChangeAppService.submitChange(dto));
    }

    /**
     * 学生查询本人的状态变更历史
     */
    @GetMapping("/my")
    public Result<List<StatusChangeVO>> my() {
        return Result.ok(statusChangeAppService.listMyChanges());
    }

    /**
     * 教师/管理员查询状态变更记录
     */
    @GetMapping("/list")
    public Result<List<StatusChangeVO>> list() {
        return Result.ok(statusChangeAppService.listChanges());
    }

    /**
     * 教师端：失业学生跟踪提醒列表
     */
    @GetMapping("/reminders")
    public Result<List<StatusChangeVO>> reminders() {
        return Result.ok(statusChangeAppService.listUnemployedReminders());
    }

    /**
     * 学生当前就业跟踪状态
     */
    @GetMapping("/my/status")
    public Result<Map<String, Object>> myStatus() {
        return Result.ok(statusChangeAppService.getMyStatus());
    }

    /**
     * 查询某毕业生的状态变更历史（教师/管理员）
     */
    @GetMapping("/graduate")
    public Result<List<StatusChangeVO>> graduateChanges(@RequestParam Long graduateId) {
        return Result.ok(statusChangeAppService.listChangesByGraduate(graduateId));
    }
}
