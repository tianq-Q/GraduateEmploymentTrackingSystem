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

package com.college.employment.config;

import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置。
 *
 * <p>职责：
 * <ul>
 *   <li>关闭 CSRF、使用无状态会话（JWT 认证，不存 Session）</li>
 *   <li>按 URL 前缀对接口做角色鉴权（admin/system → 系统管理员，admin → 主任，teacher → 教师，student → 毕业生）</li>
 *   <li>注册 JWT 过滤器 {@link JwtAuthFilter}，所有请求先过 JWT 校验再进控制器</li>
 *   <li>统一处理 401（未登录/过期）和 403（无权限）的 JSON 响应</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * 构建安全过滤链：无状态会话 + URL 级角色鉴权 + JWT 前置过滤器。
     * 接口鉴权规则以 URL 前缀划分为四个角色域（见方法内注释）。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公共接口 - 登录注册
                .antMatchers("/api/auth/**").permitAll()
                // 系统管理员 - 系统维护（账号启停、系统日志）
                .antMatchers("/api/admin/system/**").hasRole("SYSTEM_ADMIN")
                // 主任（校级管理员）- 院系/专业/班级/教师/学生管理、终审
                .antMatchers("/api/admin/**").hasRole("COLLEGE_ADMIN")
                // 教师 + 主任 - 教师功能
                .antMatchers("/api/teacher/**").hasAnyRole("TEACHER", "COLLEGE_ADMIN")
                // 毕业生 - 学生功能
                .antMatchers("/api/student/**").hasRole("GRADUATE")
                // 仪表盘 - 系统日志（系统管理员排查问题用，工作台日志卡片所有角色可见）
                .antMatchers("/api/dashboard/logs").authenticated()
                // 仪表盘 - 业务统计（主任/教师/毕业生可见；系统管理员无业务看板）
                .antMatchers("/api/dashboard/**").hasAnyRole("COLLEGE_ADMIN", "TEACHER", "GRADUATE")
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                // 未认证（匿名）访问受保护资源 -> 401
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
                })
                // 已认证但角色不足 -> 403
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"无权限访问该资源\"}");
                }))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 密码编码器：统一使用 BCrypt 哈希存储/校验密码（含内置账号初始化与改密） */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 认证管理器：供登录接口手动触发认证流程使用 */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
