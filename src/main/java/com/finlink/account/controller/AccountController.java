package com.finlink.account.controller;

import com.finlink.account.domain.dto.AccountQueryReqDTO;
import com.finlink.account.domain.dto.AccountSaveReqDTO;
import com.finlink.account.domain.vo.AccountPageVO;
import com.finlink.account.domain.vo.AccountVO;
import com.finlink.account.service.IAccountService;
import com.finlink.common.domain.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 账号管理控制器
 * <p>提供账号的增删改查、导出等 RESTful 接口</p>
 *
 * @author 稚名不带撇
 */
@Api(tags = "账号管理模块")
@RestController
@RequestMapping("/api/account")
public class AccountController {

    /**
     * 账号服务接口
     */
    @Autowired
    private IAccountService accountService;

    /**
     * 分页查询账号列表
     *
     * @param reqDTO 查询条件
     * @return 账号分页结果
     */
    @ApiOperation("分页查询账号列表")
    @GetMapping("/list")
    public Result<AccountPageVO> list(AccountQueryReqDTO reqDTO) {
        // 调用服务层执行分页查询并包装为统一响应
        return Result.success(accountService.listPage(reqDTO));
    }

    /**
     * 查询账号详情
     *
     * @param id 账号 ID
     * @return 账号详情
     */
    @ApiOperation("查询账号详情")
    @GetMapping("/{id}")
    public Result<AccountVO> getById(@ApiParam("账号 ID") @PathVariable Long id) {
        return Result.success(accountService.getById(id));
    }

    /**
     * 新增账号
     *
     * @param reqDTO 账号信息
     * @return 成功响应
     */
    @ApiOperation("新增账号")
    @PostMapping
    public Result<Void> add(@RequestBody @Validated AccountSaveReqDTO reqDTO) {
        // 调用服务层新增账号，成功后返回统一成功响应
        accountService.add(reqDTO);
        return Result.success();
    }

    /**
     * 编辑账号
     * <p>编辑账号时会同步更新流水表中的关联数据（本方账号和对方账号）</p>
     *
     * @param id     账号 ID
     * @param reqDTO 账号信息
     * @return 成功响应
     */
    @ApiOperation("编辑账号")
    @PostMapping("/{id}")
    public Result<Void> edit(@ApiParam("账号 ID") @PathVariable Long id, @RequestBody @Validated AccountSaveReqDTO reqDTO) {
        // 调用服务层编辑账号，同时会同步更新关联流水数据
        accountService.edit(id, reqDTO);
        return Result.success();
    }

    /**
     * 删除账号
     * <p>删除前会检查是否有关联的流水记录，如果有关联则不允许删除</p>
     *
     * @param id 账号 ID
     * @return 成功响应
     */
    @ApiOperation("删除账号")
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@ApiParam("账号 ID") @PathVariable Long id) {
        // 调用服务层删除账号，若存在关联流水则会抛出业务异常
        accountService.delete(id);
        return Result.success();
    }

    /**
     * 导出账号列表为 Excel
     * <p>文件名格式：账号列表_YYYYMMDD.xlsx</p>
     *
     * @param reqDTO   查询条件
     * @param response HTTP 响应
     */
    @ApiOperation("导出账号列表")
    @GetMapping("/export")
    public void export(AccountQueryReqDTO reqDTO, HttpServletResponse response) {
        accountService.export(reqDTO, response);
    }
}