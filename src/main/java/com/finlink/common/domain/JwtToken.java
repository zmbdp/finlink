package com.finlink.common.domain;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWT 认证令牌
 * <p>封装 JWT Token 字符串供 Shiro 认证使用，是 Shiro 认证流程中的身份凭证对象。
 * 本类实现了 Shiro 的 {@link AuthenticationToken} 接口，其中 Principal 和 Credentials
 * 都设置为 JWT Token 字符串本身，因为 JWT Token 本身已包含认证信息。</p>
 *
 * @author 稚名不带撇
 */
public class JwtToken implements AuthenticationToken {

    /**
     * JWT Token 字符串
     */
    private final String token;

    /**
     * 构造 JWT 认证令牌
     *
     * @param token JWT Token 字符串
     */
    public JwtToken(String token) {
        this.token = token;
    }

    /**
     * 获取身份标识（Principal）
     * <p>在 JWT 认证场景中，Principal 设置为 Token 字符串本身</p>
     *
     * @return JWT Token 字符串
     */
    @Override
    public Object getPrincipal() {
        return token;
    }

    /**
     * 获取凭证（Credentials）
     * <p>在 JWT 认证场景中，Credentials 设置为 Token 字符串本身</p>
     *
     * @return JWT Token 字符串
     */
    @Override
    public Object getCredentials() {
        return token;
    }
}