package com.finlink.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置
 * <p>从 application.yml 读取 JWT 相关配置
 *
 * @author 稚名不带撇
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥（Base64 编码）
     * <p>生产环境务必配置，避免每次启动生成新密钥
     */
    private String secret;

    /**
     * 短期 Token 有效期（毫秒）
     */
    private Long expireShort = 24 * 60 * 60 * 1000L;

    /**
     * 记住我 Token 有效期（毫秒）
     */
    private Long expireRemember = 7 * 24 * 60 * 60 * 1000L;
}
