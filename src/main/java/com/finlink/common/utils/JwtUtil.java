package com.finlink.common.utils;

import com.finlink.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * <p>提供 JWT Token 的生成、解析、验证等功能
 *
 * @author 稚名不带撇
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT 配置
     */
    private static JwtProperties jwtProperties;

    /**
     * JWT 签名密钥
     */
    private static Key KEY;

    /**
     * 初始化密钥
     */
    private static synchronized void initKey() {
        if (KEY != null) {
            return;
        }
        if (jwtProperties.getSecret() != null && !jwtProperties.getSecret().isEmpty()) {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
            KEY = Keys.hmacShaKeyFor(keyBytes);
        } else {
            log.warn("JWT 密钥未配置，使用默认密钥生成 JWT Token");
            KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    /**
     * 生成 JWT Token
     * <p>根据用户信息生成包含用户 ID 和用户名的 JWT Token，支持两种有效期
     *
     * @param userId     用户 ID
     * @param username   用户名
     * @param rememberMe 是否记住我（true：7 天有效期；false：24 小时有效期）
     * @return JWT Token 字符串
     */
    public static String createToken(Long userId, String username, boolean rememberMe) {
        // 根据是否记住我选择对应的过期时间
        long expire = rememberMe ? jwtProperties.getExpireRemember() : jwtProperties.getExpireShort();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire);

        // 构建 JWT 荷载，存放用户 ID 和用户名
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        // 生成并返回 JWT Token
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 JWT Token
     * <p>解析 Token 并获取其荷载信息（Claims）</p>
     *
     * @param token JWT Token 字符串
     * @return Token 荷载信息（Claims）
     * @throws io.jsonwebtoken.JwtException 如果 Token 无效或过期
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token 是否过期或无效
     * <p>尝试解析 Token，如果解析失败则认为 Token 已过期或无效</p>
     *
     * @param token JWT Token 字符串
     * @return true：Token 已过期或无效；false：Token 有效
     */
    public static boolean isExpired(String token) {
        // 尝试解析 Token，若解析异常则认为已过期或无效
        try {
            parseToken(token);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token 字符串
     * @return 用户名
     * @throws io.jsonwebtoken.JwtException 如果 Token 无效或过期
     */
    public static String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从 Token 中获取用户 ID
     *
     * @param token JWT Token 字符串
     * @return 用户 ID
     * @throws io.jsonwebtoken.JwtException 如果 Token 无效或过期
     */
    public static Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 注入 JwtProperties
     */
    @Autowired
    public void setJwtProperties(JwtProperties jwtProperties) {
        JwtUtil.jwtProperties = jwtProperties;
        initKey();
    }
}