package com.finlink.common.shiro;

import com.finlink.common.domain.JwtToken;
import com.finlink.common.domain.ResultCode;
import com.finlink.common.utils.JwtUtil;
import com.finlink.user.domain.entity.User;
import com.finlink.user.service.IUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义 Shiro Realm
 * <p>实现用户认证和授权逻辑，支持用户名密码认证和JWT认证</p>
 *
 * @author 稚名不带撇
 */
public class UserRealm extends AuthorizingRealm {

    /**
     * 用户服务接口
     */
    @Autowired
    private IUserService userService;

    /**
     * 支持两种 Token 类型
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken || token instanceof JwtToken;
    }

    /**
     * 授权（当前未实现角色权限控制）
     *
     * @param principals 用户身份标识
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 认证
     * <p>支持用户名密码认证和JWT认证两种方式</p>
     *
     * @param token 认证令牌
     * @return 认证信息
     * @throws AuthenticationException 认证失败时抛出
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // JWT Token 认证
        if (token instanceof JwtToken) {
            String jwt = (String) token.getPrincipal();
            String username = JwtUtil.getUsername(jwt);

            // 查询用户是否存在
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UnknownAccountException(ResultCode.ERROR_PHONE_FORMAT.getErrMsg());
            }

            if (user.getStatus() != null && user.getStatus() == 0) {
                throw new LockedAccountException("账号已被禁用");
            }

            return new SimpleAuthenticationInfo(username, jwt, getName());
        }

        // 用户名密码认证（原有逻辑）
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UnknownAccountException(ResultCode.ERROR_PHONE_FORMAT.getErrMsg());
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new LockedAccountException("账号已被禁用");
        }

        return new SimpleAuthenticationInfo(
                user.getUsername(),
                user.getPassword(),
                getName()
        );
    }
}