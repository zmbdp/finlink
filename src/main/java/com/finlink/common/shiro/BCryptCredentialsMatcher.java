package com.finlink.common.shiro;

import com.finlink.common.domain.JwtToken;
import com.finlink.common.utils.BCryptUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

/**
 * BCrypt 密码匹配器
 * <p>使用 BCrypt 算法验证密码</p>
 *
 * @author 稚名不带撇
 */
public class BCryptCredentialsMatcher implements CredentialsMatcher {

    /**
     * 验证密码是否匹配
     *
     * @param token 用户提交的认证令牌
     * @param info  从数据库查询到的用户信息
     * @return 密码是否匹配
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // 👇 加了这 3 行：JWT Token 不需要密码校验，直接放行
        if (token instanceof JwtToken) {
            return true;
        }

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String plainPassword = new String(upToken.getPassword());
        String hashedPassword = (String) info.getCredentials();

        return BCryptUtil.checkPassword(plainPassword, hashedPassword);
    }
}
