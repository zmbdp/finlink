package com.finlink.common.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

/**
 * JWT 密钥生成工具
 * <p>用于生成符合要求的 JWT 签名密钥
 *
 * @author 稚名不带撇
 */
public class JwtSecretGenerator {

    /**
     * 生成 JWT 签名密钥（Base64 编码）
     * <p>生成一个符合 HS256 要求的密钥并转换为 Base64 字符串
     *
     * @return Base64 编码的密钥字符串
     */
    public static String generateSecret() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 主方法 - 直接运行生成密钥
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String secret = generateSecret();
        System.out.println("生成的 JWT 密钥（复制到配置文件）:");
        System.out.println(secret);
    }
}