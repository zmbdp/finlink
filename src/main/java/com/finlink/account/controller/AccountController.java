package com.finlink.account.controller;

import com.finlink.account.domain.dto.AccountQueryReqDTO;
import com.finlink.account.domain.dto.AccountSaveReqDTO;
import com.finlink.account.domain.vo.AccountPageVO;
import com.finlink.account.domain.vo.AccountVO;
import com.finlink.account.service.IAccountService;
import com.finlink.common.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 账号管理控制器
 *
 * @author 稚名不带撇
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private IAccountService accountService;

    /**
     * 分页查询账号列表
     *
     * @param reqDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<AccountPageVO> list(AccountQueryReqDTO reqDTO) {
        return Result.success(accountService.listPage(reqDTO));
    }

    /**
     * 查询账号详情
     *
     * @param id 账号 ID
     * @return 账号详情
     */
    @GetMapping("/{id}")
    public Result<AccountVO> getById(@PathVariable Long id) {
        return Result.success(accountService.getById(id));
    }

    /**
     * 新增账号
     *
     * @param reqDTO 账号信息
     * @return 成功响应
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Validated AccountSaveReqDTO reqDTO) {
        accountService.add(reqDTO);
        return Result.success();
    }

    /**
     * 编辑账号
     * <p>重点：编辑账号时会同步更新流水表中的关联数据</p>
     *
     * @param id     账号 ID
     * @param reqDTO 账号信息
     * @return 成功响应
     */
    @PostMapping("/{id}")
    public Result<Void> edit(@PathVariable Long id, @RequestBody @Validated AccountSaveReqDTO reqDTO) {
        accountService.edit(id, reqDTO);
        return Result.success();
    }

    /**
     * 删除账号
     *
     * @param id 账号 ID
     * @return 成功响应
     */
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return Result.success();
    }

    /**
     * 导出账号列表为 Excel
     *
     * @param reqDTO   查询条件
     * @param response HTTP 响应
     */
    @GetMapping("/export")
    public void export(AccountQueryReqDTO reqDTO, HttpServletResponse response) {
        accountService.export(reqDTO, response);
    }
}