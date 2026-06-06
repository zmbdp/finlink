package com.finlink.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.finlink.common.utils.JwtUtil;
import com.finlink.user.domain.dto.LoginDTO;
import com.finlink.user.domain.dto.LoginReqDTO;
import com.finlink.user.domain.entity.User;
import com.finlink.user.mapper.UserMapper;
import com.finlink.user.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * <p>实现用户相关的业务逻辑</p>
 *
 * @author 稚名不带撇
 */
@Service
public class UserServiceImpl implements IUserService {

    /**
     * 用户数据访问层
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息实体
     */
    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(User::getUsername, username)
                .last("limit 1");
        return userMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 用户登录
     * <p>使用 Shiro 进行认证，返回JWT Token</p>
     *
     * @param loginReqDTO 登录请求参数
     * @return 登录成功后的凭证信息
     */
    @Override
    public LoginDTO login(LoginReqDTO loginReqDTO) {
        String username = loginReqDTO.getUsername();
        String password = loginReqDTO.getPassword();
        Boolean rememberMe = loginReqDTO.getRememberMe();

        // 使用 Shiro 进行登录认证，直接传入明文密码由 BCryptCredentialsMatcher 负责验证
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        subject.login(token);

        // 认证成功，生成 JWT Token
        User user = findByUsername(username);
        boolean isRememberMe = rememberMe != null && rememberMe;
        String jwtToken = JwtUtil.createToken(
                user.getId(),  // 用户 ID
                username, // 用户名
                isRememberMe // 是否记住我
        );

        // 组装返回数据
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setToken(jwtToken);  // 返回 JWT Token 而不是 Session ID
        loginDTO.setNickname(user.getNickname());
        return loginDTO;
    }

    /**
     * 用户登出
     * <p>清除当前 Shiro 会话</p>
     */
    @Override
    public void logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
    }
}