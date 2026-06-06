package com.finlink.user.controller;

import com.finlink.common.domain.Result;
import com.finlink.common.domain.ResultCode;
import com.finlink.common.utils.BeanCopyUtil;
import com.finlink.user.domain.dto.LoginDTO;
import com.finlink.user.domain.dto.LoginReqDTO;
import com.finlink.user.domain.vo.LoginVO;
import com.finlink.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 * <p>处理登录、登出等认证相关请求</p>
 *
 * @author 稚名不带撇
 */
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
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Validated LoginReqDTO loginReqDTO) {
        try {
            LoginDTO loginDTO = userService.login(loginReqDTO);
            return Result.success(BeanCopyUtil.copyProperties(loginDTO, LoginVO.class));
        } catch (Exception e) {
            return Result.fail(ResultCode.ERROR_PHONE_FORMAT.getCode(), ResultCode.ERROR_PHONE_FORMAT.getErrMsg());
        }
    }

    /**
     * 用户登出
     * <p>调用 Service 层清除当前用户会话，退出登录状态</p>
     *
     * @return 登出成功响应
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    /**
     * 未授权处理接口
     * <p>当用户未登录或权限不足时返回统一错误信息</p>
     */
    @GetMapping("/unauth")
    public Result<Void> unauth() {
        return Result.fail(ResultCode.LOGIN_STATUS_OVERTIME.getCode(), ResultCode.LOGIN_STATUS_OVERTIME.getErrMsg());
    }
}