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

package com.college.employment.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 工具类：签发与解析登录令牌。
 *
 * <p>密钥与有效期来自 {@code application.yml} 的 {@code jwt.secret} / {@code jwt.expiration}。
 * token 中携带 userId、role、deptId 三个声明，登录成功后返回前端缓存，
 * 每次请求经 {@link com.college.employment.config.JwtAuthFilter} 校验。</p>
 */
@Component
public class JwtUtil {

    /** 签名密钥（HMAC-SHA） */
    private final SecretKey key;
    /** 令牌有效期（毫秒） */
    private final long expiration;

    /** 构造时从配置读取密钥并解码为 SecretKey */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    /** 签发 token：以用户名做 subject，附带 userId/role/deptId 声明 */
    public String generateToken(Long userId, String username, String role, Long deptId) {
        Date now = new Date();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("deptId", deptId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    /** 解析并校验 token 签名，失败抛 JwtException */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** 从 token 取用户 ID */
    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /** 从 token 取登录账号（学号/工号） */
    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    /** 从 token 取角色编码 */
    public String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    /** 从 token 取所属院系 ID（可能为 null） */
    public Long getDeptId(String token) {
        Object val = parseToken(token).get("deptId");
        if (val == null) return null;
        return ((Number) val).longValue();
    }

    /** 校验 token 是否有效（签名正确且未过期） */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
