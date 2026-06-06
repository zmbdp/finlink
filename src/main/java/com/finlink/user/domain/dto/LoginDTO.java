package com.finlink.user.domain.dto;

import lombok.Data;

/**
 * 登录响应 DTO
 * <p>登录成功后返回给前端的凭证数据</p>
 *
 * @author 稚名不带撇
 */
@Data
public class LoginDTO {

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