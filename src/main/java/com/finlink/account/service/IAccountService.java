package com.finlink.account.service;

import com.finlink.account.domain.dto.AccountQueryReqDTO;
import com.finlink.account.domain.dto.AccountSaveReqDTO;
import com.finlink.account.domain.vo.AccountPageVO;
import com.finlink.account.domain.vo.AccountVO;

import javax.servlet.http.HttpServletResponse;

/**
 * 账号服务接口
 *
 * @author 稚名不带撇
 */
public interface IAccountService {

    /**
     * 分页查询账号列表
     *
     * @param reqDTO 查询条件
     * @return 分页结果
     */
    AccountPageVO listPage(AccountQueryReqDTO reqDTO);

    /**
     * 查询账号详情
     *
     * @param id 账号 ID
     * @return 账号详情
     */
    AccountVO getById(Long id);

    /**
     * 新增账号
     *
     * @param reqDTO 账号信息
     */
    void add(AccountSaveReqDTO reqDTO);

    /**
     * 编辑账号（含流水数据同步）
     *
     * @param id     账号 ID
     * @param reqDTO 账号信息
     */
    void edit(Long id, AccountSaveReqDTO reqDTO);

    /**
     * 删除账号
     *
     * @param id 账号 ID
     */
    void delete(Long id);

    /**
     * 导出账号列表为 Excel
     *
     * @param reqDTO   查询条件
     * @param response HTTP 响应
     */
    void export(AccountQueryReqDTO reqDTO, HttpServletResponse response);
}