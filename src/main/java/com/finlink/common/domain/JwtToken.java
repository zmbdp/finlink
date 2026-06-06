package com.finlink.common.domain;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWT 认证令牌
 * <p>封装 JWT Token 供 Shiro 认证使用</p>
 *
 * @author 稚名不带撇
 */
public class JwtToken implements AuthenticationToken {

    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}