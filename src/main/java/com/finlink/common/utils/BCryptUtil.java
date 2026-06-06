package com.finlink.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt 密码加密工具类
 *
 * @author 稚名不带撇
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BCryptUtil {

    /**
     * 加密密码
     *
     * @param plainPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码
     *
     * @param plainPassword  明文密码
     * @param hashedPassword BCrypt 加密后的密码
     * @return 密码是否匹配
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}