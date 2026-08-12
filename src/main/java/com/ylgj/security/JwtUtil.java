package com.ylgj.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 负责令牌的生成、解析与校验
 */
@Component
public class JwtUtil {

    /** 密钥（从配置读取，支持环境变量覆盖） */
    @Value("${jwt.secret:ylgj-health-management-secret-key-2026}")
    private String secret;

    /** 过期时间（秒） */
    @Value("${jwt.expire:7200}")
    private long expire;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param username 用户名
     * @param userId   用户ID
     */
    public String generateToken(String username, Integer userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expire * 1000);
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，签名或过期校验失败时抛异常
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Integer getUserId(String token) {
        Object userId = parseToken(token).get("userId");
        return userId == null ? null : Integer.valueOf(userId.toString());
    }

    /**
     * 校验 Token 是否有效（签名正确且未过期）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取过期时间
     */
    public long getExpire() {
        return expire;
    }
}
