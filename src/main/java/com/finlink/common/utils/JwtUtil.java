package com.finlink.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * <p>生成和解析JWT Token</p>
 *
 * @author 稚名不带撇
 */
public class JwtUtil {

    // 密钥（生产环境应放配置文件）
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 短期有效期：24小时
    private static final long EXPIRE_SHORT = 24 * 60 * 60 * 1000L;
    // 记住我有效期：7天
    private static final long EXPIRE_REMEMBER = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成 Token
     * @param userId 用户ID
     * @param username 用户名
     * @param rememberMe 是否记住我
     * @return JWT Token
     */
    public static String createToken(Long userId, String username, boolean rememberMe) {
        long expire = rememberMe ? EXPIRE_REMEMBER : EXPIRE_SHORT;
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 Token
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token 是否过期
     */
    public static boolean isExpired(String token) {
        try {
            parseToken(token);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从 Token 获取用户名
     */
    public static String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从 Token 获取用户ID
     */
    public static Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }
}