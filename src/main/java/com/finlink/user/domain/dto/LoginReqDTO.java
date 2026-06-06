package com.finlink.user.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * <p>接收前端登录请求的参数对象</p>
 *
 * @author 稚名不带撇
 */
@Data
public class LoginReqDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 记住我（7天免登录）
     */
    private Boolean rememberMe;
}