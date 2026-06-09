package com.finlink.common.security;

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
 * <p>实现用户认证和授权逻辑，支持用户名密码认证和 JWT 认证两种方式</p>
 *
 * @author 稚名不带撇
 */
public class UserRealm extends AuthorizingRealm {

    /**
     * 用户服务接口，用于查询用户信息
     */
    @Autowired
    private IUserService userService;

    /**
     * 判断是否支持指定的 Token 类型
     * <p>支持 UsernamePasswordToken 和 JwtToken 两种类型</p>
     *
     * @param token 认证令牌
     * @return true：支持；false：不支持
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken || token instanceof JwtToken;
    }

    /**
     * 授权（当前未实现角色权限控制）
     * <p>预留方法，后续可用于实现角色和权限的授权逻辑</p>
     *
     * @param principals 用户身份标识集合
     * @return 授权信息（当前返回 null）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 认证
     * <p>支持用户名密码认证和 JWT 认证两种方式：
     * <ul>
     *     <li>JWT Token 认证：解析 Token 获取用户名，验证用户存在且状态正常</li>
     *     <li>用户名密码认证：查询用户信息，验证用户名和密码，检查账号状态</li>
     * </ul>
     *
     * @param token 认证令牌（JwtToken 或 UsernamePasswordToken）
     * @return 认证信息
     * @throws UnknownAccountException 用户不存在
     * @throws LockedAccountException  账号已被禁用
     * @throws AuthenticationException 其他认证失败异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // JWT Token 认证
        if (token instanceof JwtToken) {
            String jwt = (String) token.getPrincipal();
            String username = JwtUtil.getUsername(jwt);

            // 查询用户是否存在
            findAndValidateUser(username);

            return new SimpleAuthenticationInfo(username, jwt, getName());
        }

        // 用户名密码认证（原有逻辑）
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();


        User user = findAndValidateUser(username);

        return new SimpleAuthenticationInfo(
                user.getUsername(),
                user.getPassword(),
                getName()
        );
    }

    /**
     * 根据用户名查询并校验用户登录资格
     * <p>
     * 查询用户信息并进行登录前的合法性校验，包括：
     * <ul>
     *     <li>用户存在性校验：用户不存在时抛出 {@link UnknownAccountException}</li>
     *     <li>账号状态校验：账号被禁用时抛出 {@link LockedAccountException}</li>
     * </ul>
     *
     * @param username 用户名
     * @return 校验通过的用户实体对象，包含完整的用户信息
     * @throws UnknownAccountException 用户不存在时抛出
     * @throws LockedAccountException  账号被禁用（status = 0）时抛出
     */
    private User findAndValidateUser(String username) {
        // 根据用户名查询用户信息
        User user = userService.findByUsername(username);

        // 用户不存在，抛出未知账号异常
        if (user == null) {
            throw new UnknownAccountException(ResultCode.USERNAME_OR_PASSWORD_ERROR.getErrMsg());
        }

        // 校验账号状态，被禁用则抛出锁定异常
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new LockedAccountException(ResultCode.ACCOUNT_LOCKED.getErrMsg());
        }
        return user;
    }
}