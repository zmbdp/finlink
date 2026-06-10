package com.finlink.user.controller;

import com.finlink.common.domain.Result;
import com.finlink.common.domain.ResultCode;
import com.finlink.common.utils.BeanCopyUtil;
import com.finlink.user.domain.dto.LoginDTO;
import com.finlink.user.domain.dto.LoginReqDTO;
import com.finlink.user.domain.vo.LoginVO;
import com.finlink.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 * <p>处理登录、登出等认证相关请求</p>
 *
 * @author 稚名不带撇
 */
@Api(tags = "用户认证模块")
@RestController
@RequestMapping("/api")
public class UserController {

    /**
     * 用户服务接口
     */
    @Autowired
    private IUserService userService;

    /**
     * 用户登录
     * <p>接收前端登录请求，校验参数后调用 Service 层完成认证，返回登录凭证</p>
     *
     * @param loginReqDTO 登录请求参数，包含用户名、密码及记住我选项
     * @return 登录成功返回包含 token 的响应数据
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Validated LoginReqDTO loginReqDTO) {
        // 调用服务层完成登录认证，并将 DTO 转换为 VO 返回
        LoginDTO loginDTO = userService.login(loginReqDTO);
        return Result.success(BeanCopyUtil.copyProperties(loginDTO, LoginVO.class));
    }

    /**
     * 用户登出
     * <p>调用 Service 层清除当前用户会话，退出登录状态</p>
     *
     * @return 登出成功响应
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    /**
     * 未授权处理接口
     * <p>当用户未登录或权限不足时返回统一错误信息</p>
     */
    @ApiOperation("未授权处理")
    @GetMapping("/unauth")
    public Result<Void> unauth() {
        return Result.fail(ResultCode.LOGIN_STATUS_OVERTIME.getCode(), ResultCode.LOGIN_STATUS_OVERTIME.getErrMsg());
    }
}