package com.finlink.user.domain.vo;

import lombok.Data;

/**
 * 登录响应 VO
 * <p>返回给前端的登录结果视图对象</p>
 *
 * @author 稚名不带撇
 */
@Data
public class LoginVO {

    /**
     * 会话 token，后续前端访问需携带
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;
}