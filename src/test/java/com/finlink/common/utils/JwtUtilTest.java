package com.finlink.common.utils;

import com.finlink.common.config.JwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link JwtUtil} 集成测试类。
 * <p>
 * 从 Spring 容器中获取真实的 {@link JwtProperties} 配置进行测试，
 * 若配置中未设置密钥，则自动注入测试密钥以保证测试可运行。
 * </p>
 *
 * @author 稚名不带撇
 */
@Rollback
@Transactional
@SpringBootTest
@DisplayName("JWT工具集成测试")
class JwtUtilTest {

    /**
     * JWT 配置属性，从 Spring 容器中自动注入。
     */
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 确保 {@link JwtProperties} 中已配置有效的 JWT 密钥。
     * <p>
     * 若当前环境未配置密钥，则使用固定的测试密钥进行初始化，避免测试因密钥缺失而失败。
     * </p>
     */
    private void ensureSecret() {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().isEmpty()) {
            String testSecret = "testSecretKey1234567890abcdefghijklmnopqrstuvwxyz";
            jwtProperties.setSecret(Base64.getEncoder().encodeToString(testSecret.getBytes()));
        }
    }

    /**
     * 测试生成标准 JWT Token。
     * <p>
     * 期望：返回非空字符串，且格式符合 JWT 规范（包含两个点分隔符）。
     * </p>
     */
    @Test
    @DisplayName("测试生成 JWT Token")
    void testCreateToken() {
        ensureSecret();

        Long userId = 1L;
        String username = "testuser";
        boolean rememberMe = false;

        String token = JwtUtil.createToken(userId, username, rememberMe);

        assertNotNull(token, "Token 不应为空");
        assertFalse(token.isEmpty(), "Token 不应为空字符串");
        // JWT Token 应该包含两个点分隔符
        assertTrue(token.chars().filter(ch -> ch == '.').count() == 2, "Token 应包含两个点分隔符");
    }

    /**
     * 测试生成"记住我"模式的 JWT Token。
     * <p>
     * 期望：返回非空字符串，且格式符合 JWT 规范。
     * </p>
     */
    @Test
    @DisplayName("测试生成记住我的 Token")
    void testCreateTokenWithRememberMe() {
        ensureSecret();

        Long userId = 1L;
        String username = "testuser";
        boolean rememberMe = true;

        String token = JwtUtil.createToken(userId, username, rememberMe);

        assertNotNull(token, "Token 不应为空");
        assertFalse(token.isEmpty(), "Token 不应为空字符串");
    }

    /**
     * 测试从 JWT Token 中解析用户名。
     * <p>
     * 期望：解析出的用户名与生成 Token 时传入的用户名一致。
     * </p>
     */
    @Test
    @DisplayName("测试解析 JWT Token 获取用户名")
    void testGetUsername() {
        ensureSecret();

        Long userId = 1L;
        String username = "testuser";
        boolean rememberMe = false;

        String token = JwtUtil.createToken(userId, username, rememberMe);
        String extractedUsername = JwtUtil.getUsername(token);

        assertEquals(username, extractedUsername, "解析出的用户名应与原始值一致");
    }

    /**
     * 测试从 JWT Token 中解析用户 ID。
     * <p>
     * 期望：解析出的用户 ID 与生成 Token 时传入的用户 ID 一致。
     * </p>
     */
    @Test
    @DisplayName("测试解析 JWT Token 获取用户 ID")
    void testGetUserId() {
        ensureSecret();

        Long userId = 1L;
        String username = "testuser";
        boolean rememberMe = false;

        String token = JwtUtil.createToken(userId, username, rememberMe);
        Long extractedUserId = JwtUtil.getUserId(token);

        assertEquals(userId, extractedUserId, "解析出的用户 ID 应与原始值一致");
    }

    /**
     * 测试校验有效的 JWT Token 是否过期。
     * <p>
     * 期望：刚生成的 Token 不应被判定为过期。
     * </p>
     */
    @Test
    @DisplayName("测试校验 Token - 有效 Token")
    void testIsExpired_ValidToken() {
        ensureSecret();

        Long userId = 1L;
        String username = "testuser";

        String token = JwtUtil.createToken(userId, username, false);

        assertFalse(JwtUtil.isExpired(token), "有效的 Token 不应被判定为过期");
    }

    /**
     * 测试校验无效的 JWT Token 是否过期。
     * <p>
     * 期望：格式非法的 Token 应被判定为过期（或解析失败）。
     * </p>
     */
    @Test
    @DisplayName("测试校验 Token - 无效 Token")
    void testIsExpired_InvalidToken() {
        ensureSecret();

        String invalidToken = "invalid.token.here";

        assertTrue(JwtUtil.isExpired(invalidToken), "无效的 Token 应被判定为过期");
    }

    /**
     * 测试校验空或 null 的 JWT Token 是否过期。
     * <p>
     * 期望：null 和空字符串均应被判定为过期。
     * </p>
     */
    @Test
    @DisplayName("测试校验 Token - 空 Token")
    void testIsExpired_EmptyToken() {
        ensureSecret();

        assertTrue(JwtUtil.isExpired(null), "null Token 应被判定为过期");
        assertTrue(JwtUtil.isExpired(""), "空字符串 Token 应被判定为过期");
    }
}