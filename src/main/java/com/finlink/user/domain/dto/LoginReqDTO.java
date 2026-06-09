package com.finlink.user.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * <p>接收前端登录请求的参数对象</p>
 *
 * @author 稚名不带撇
 */
@Data
@ApiModel("登录请求参数")
public class LoginReqDTO {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true, example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true, example = "admin123")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 记住我（7天免登录）
     */
    @ApiModelProperty(value = "记住我（7天免登录）", example = "false")
    private Boolean rememberMe;
}