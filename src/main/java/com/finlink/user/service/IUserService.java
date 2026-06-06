package com.finlink.user.service;

import com.finlink.user.domain.dto.LoginDTO;
import com.finlink.user.domain.dto.LoginReqDTO;
import com.finlink.user.domain.entity.User;

/**
 * 用户服务接口
 * <p>定义用户相关的业务逻辑，包括登录、登出、查询等</p>
 *
 * @author 稚名不带撇
 */
public interface IUserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息实体
     */
    User findByUsername(String username);

    /**
     * 用户登录
     * <p>调用 Shiro 完成认证，返回登录凭证</p>
     *
     * @param loginReqDTO 登录请求参数
     * @return 登录成功后的凭证信息
     */
    LoginDTO login(LoginReqDTO loginReqDTO);

    /**
     * 用户登出
     * <p>清除当前用户 Shiro 会话</p>
     */
    void logout();
}