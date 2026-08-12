package com.college.employment.controller;

import com.college.employment.common.api.Result;
import com.college.employment.common.constant.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/stats")
    @PreAuthorize(RoleConstants.HAS_ANY)
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalGraduates", 2486);
        data.put("employedCount", 2103);
        data.put("unemployedCount", 383);
        data.put("employmentRate", 84.6);
        data.put("pendingReviews", 56);
        data.put("totalDepartments", 7);
        return Result.ok(data);
    }
}
