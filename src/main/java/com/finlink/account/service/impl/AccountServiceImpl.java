package com.finlink.account.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.finlink.account.domain.dto.AccountQueryReqDTO;
import com.finlink.account.domain.dto.AccountSaveReqDTO;
import com.finlink.account.domain.entity.Account;
import com.finlink.account.domain.vo.AccountExcelVO;
import com.finlink.account.domain.vo.AccountPageVO;
import com.finlink.account.domain.vo.AccountVO;
import com.finlink.account.mapper.AccountMapper;
import com.finlink.account.service.IAccountService;
import com.finlink.common.constants.CommonConstants;
import com.finlink.common.exception.ServiceException;
import com.finlink.common.utils.BeanCopyUtil;
import com.finlink.flow.mapper.TransactionFlowMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 账号服务实现类
 * <p>实现账号的增删改查、导出等功能，包含账号编辑时的流水数据同步更新</p>
 *
 * @author 稚名不带撇
 */
@Service
public class AccountServiceImpl implements IAccountService {

    /**
     * 账号数据访问接口
     */
    @Autowired
    private AccountMapper accountMapper;

    /**
     * 流水数据访问接口，用于账号编辑时的同步更新
     */
    @Autowired
    private TransactionFlowMapper transactionFlowMapper;

    /**
     * 分页查询账号列表
     * <p>根据查询条件构建查询 wrapper，执行分页查询，返回分页结果</p>
     *
     * @param reqDTO 查询条件
     * @return 账号分页结果
     */
    @Override
    public AccountPageVO listPage(AccountQueryReqDTO reqDTO) {
        // 构建查询条件并执行分页查询
        LambdaQueryWrapper<Account> wrapper = buildQueryWrapper(reqDTO);
        Page<Account> page = accountMapper.selectPage(
                new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize()), wrapper);

        // 将实体列表转换为 VO 列表并组装分页结果
        AccountPageVO pageVO = new AccountPageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setRecords(BeanCopyUtil.copyListProperties(page.getRecords(), AccountVO.class));
        return pageVO;
    }

    /**
     * 根据 ID 查询账号详情
     *
     * @param id 账号 ID
     * @return 账号详情
     * @throws ServiceException 如果账号不存在
     */
    @Override
    public AccountVO getById(Long id) {
        // 根据 ID 查询账号，不存在则抛出业务异常
        Account account = accountMapper.selectById(id);
        if (account == null) {
            throw new ServiceException(CommonConstants.ERROR_MSG_ACCOUNT_NOT_FOUND);
        }
        return BeanCopyUtil.copyProperties(account, AccountVO.class);
    }

    /**
     * 新增账号
     * <p>校验账号唯一性，插入新账号记录</p>
     *
     * @param reqDTO 账号信息
     * @throws ServiceException 如果账号已存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AccountSaveReqDTO reqDTO) {
        // 校验账号唯一性：同一账号号不允许重复
        Long count = accountMapper.selectCount(
                new LambdaQueryWrapper<Account>()
                        .eq(Account::getAccountNo, reqDTO.getAccountNo())
        );
        if (count > 0) {
            throw new ServiceException(CommonConstants.ERROR_MSG_ACCOUNT_EXISTS);
        }

        // 拷贝属性并插入新账号记录
        Account account = BeanCopyUtil.copyProperties(reqDTO, Account.class);
        accountMapper.insert(account);
    }

    /**
     * 编辑账号（含流水数据同步更新）
     * <p>更新账号信息，同时同步更新流水表中的本方账号和对方账号信息</p>
     *
     * @param id     账号 ID
     * @param reqDTO 账号信息
     * @throws ServiceException 如果账号不存在或新账号已存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, AccountSaveReqDTO reqDTO) {
        // 查询旧记录，不存在则抛出异常
        Account old = accountMapper.selectById(id);
        if (old == null) {
            throw new ServiceException(CommonConstants.ERROR_MSG_ACCOUNT_NOT_FOUND);
        }

        // 如果账号号发生变更，需校验新账号号的唯一性
        if (!old.getAccountNo().equals(reqDTO.getAccountNo())) {
            Long count = accountMapper.selectCount(
                    new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, reqDTO.getAccountNo())
            );
            if (count > 0) {
                throw new ServiceException(CommonConstants.ERROR_MSG_ACCOUNT_EXISTS);
            }
        }

        // 更新账号表基本信息
        Account account = BeanCopyUtil.copyProperties(reqDTO, Account.class);
        account.setId(id);
        accountMapper.updateById(account);

        // 同步更新流水表中的本方账号相关信息（公司、银行、币种）
        transactionFlowMapper.syncUpdateByAccountNo(
                old.getAccountNo(),
                reqDTO.getAccountNo(),
                reqDTO.getCompany(),
                reqDTO.getBank(),
                reqDTO.getCurrency()
        );

        // 同步更新流水表中的对方账号信息（其他账号流水中对方为当前账号的场景）
        transactionFlowMapper.syncUpdateByCounterpartAccountNo(
                old.getAccountNo(),
                reqDTO.getAccountNo(),
                reqDTO.getBank()
        );
    }

    /**
     * 删除账号
     * <p>先检查是否有关联的流水记录，如果有关联则不允许删除</p>
     *
     * @param id 账号 ID
     * @throws ServiceException 如果账号不存在或已关联流水记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 查询待删除账号，不存在则抛出异常
        Account account = accountMapper.selectById(id);
        if (account == null) {
            throw new ServiceException(CommonConstants.ERROR_MSG_ACCOUNT_NOT_FOUND);
        }

        // 检查是否有关联流水记录，若存在则禁止删除
        int flowCount = transactionFlowMapper.countByAccountNo(account.getAccountNo());
        if (flowCount > 0) {
            throw new ServiceException(String.format(CommonConstants.ERROR_MSG_ACCOUNT_HAS_FLOWS, flowCount));
        }

        // 执行删除
        accountMapper.deleteById(id);
    }

    /**
     * 导出账号列表为 Excel
     * <p>根据查询条件查询数据，导出为 Excel 文件，文件名格式：账号列表_YYYYMMDD.xlsx</p>
     *
     * @param reqDTO   查询条件
     * @param response HTTP 响应
     * @throws ServiceException 如果 Excel 导出失败
     */
    @Override
    public void export(AccountQueryReqDTO reqDTO, HttpServletResponse response) {
        // 查询符合条件的账号数据并转换为 Excel VO
        LambdaQueryWrapper<Account> wrapper = buildQueryWrapper(reqDTO);
        List<Account> list = accountMapper.selectList(wrapper);
        List<AccountExcelVO> voList = BeanCopyUtil.copyListProperties(list, AccountExcelVO.class);

        // 组装文件名并设置响应头，触发浏览器下载
        String fileName = CommonConstants.EXCEL_FILE_NAME_ACCOUNT + LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT_SHORT)) + CommonConstants.EXCEL_FILE_SUFFIX;
        response.setContentType(CommonConstants.EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(CommonConstants.UTF8);
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, CommonConstants.UTF8).replace("+", "%20"));
            EasyExcel.write(response.getOutputStream(), AccountExcelVO.class).sheet(CommonConstants.EXCEL_SHEET_NAME_ACCOUNT).doWrite(voList);
        } catch (IOException e) {
            throw new ServiceException(CommonConstants.ERROR_MSG_EXCEL_EXPORT_FAILED);
        }
    }

    /**
     * 构建账号查询条件 Wrapper。
     * <p>根据请求 DTO 构建 MyBatis-Plus 的查询 wrapper，支持模糊匹配和精确匹配</p>
     *
     * @param reqDTO 前端传入的查询参数
     * @return 组装后的 MyBatis-Plus 查询 Wrapper
     */
    private LambdaQueryWrapper<Account> buildQueryWrapper(AccountQueryReqDTO reqDTO) {
        return new LambdaQueryWrapper<Account>()
                .like(StringUtils.isNotBlank(reqDTO.getAccountNo()), Account::getAccountNo, reqDTO.getAccountNo())
                .like(StringUtils.isNotBlank(reqDTO.getCompany()), Account::getCompany, reqDTO.getCompany())
                .like(StringUtils.isNotBlank(reqDTO.getBank()), Account::getBank, reqDTO.getBank())
                .eq(StringUtils.isNotBlank(reqDTO.getDataSource()), Account::getDataSource, reqDTO.getDataSource())
                .orderByDesc(Account::getCreateTime);
    }
}