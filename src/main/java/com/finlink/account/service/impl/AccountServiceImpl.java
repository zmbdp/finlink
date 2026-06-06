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
 *
 * @author 稚名不带撇
 */
@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TransactionFlowMapper transactionFlowMapper;

    @Override
    public AccountPageVO listPage(AccountQueryReqDTO reqDTO) {
        LambdaQueryWrapper<Account> wrapper = buildQueryWrapper(reqDTO);
        Page<Account> page = accountMapper.selectPage(
                new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize()), wrapper);

        AccountPageVO pageVO = new AccountPageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setRecords(BeanCopyUtil.copyListProperties(page.getRecords(), AccountVO.class));
        return pageVO;
    }

    @Override
    public AccountVO getById(Long id) {
        Account account = accountMapper.selectById(id);
        if (account == null) {
            throw new ServiceException("账号不存在");
        }
        return BeanCopyUtil.copyProperties(account, AccountVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AccountSaveReqDTO reqDTO) {
        // 校验账号唯一性
        Long count = accountMapper.selectCount(
                new LambdaQueryWrapper<Account>()
                        .eq(Account::getAccountNo, reqDTO.getAccountNo())
        );
        if (count > 0) {
            throw new ServiceException("账号已存在");
        }
        Account account = BeanCopyUtil.copyProperties(reqDTO, Account.class);
        accountMapper.insert(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, AccountSaveReqDTO reqDTO) {
        Account old = accountMapper.selectById(id);
        if (old == null) {
            throw new ServiceException("账号不存在");
        }

        // 如果账号号发生变更，校验新账号唯一性
        if (!old.getAccountNo().equals(reqDTO.getAccountNo())) {
            Long count = accountMapper.selectCount(
                    new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, reqDTO.getAccountNo()));
            if (count > 0) {
                throw new ServiceException("账号已存在");
            }
        }

        // 更新账号表
        Account account = BeanCopyUtil.copyProperties(reqDTO, Account.class);
        account.setId(id);
        accountMapper.updateById(account);

        // 同步更新流水表本方账号信息
        transactionFlowMapper.syncUpdateByAccountNo(
                old.getAccountNo(),
                reqDTO.getAccountNo(),
                reqDTO.getCompany(),
                reqDTO.getBank(),
                reqDTO.getCurrency()
        );

        // 同步更新流水表对方账号信息（其他账号的流水中，对方是当前账号的也需要更新）
        transactionFlowMapper.syncUpdateByCounterpartAccountNo(
                old.getAccountNo(),
                reqDTO.getAccountNo(),
                reqDTO.getBank()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Account account = accountMapper.selectById(id);
        if (account == null) {
            throw new ServiceException("账号不存在");
        }

        // 检查是否有关联的流水记录
        int flowCount = transactionFlowMapper.countByAccountNo(account.getAccountNo());
        if (flowCount > 0) {
            throw new ServiceException("该账号已关联 " + flowCount + " 条流水记录，无法删除");
        }

        accountMapper.deleteById(id);
    }

    @Override
    public void export(AccountQueryReqDTO reqDTO, HttpServletResponse response) {
        LambdaQueryWrapper<Account> wrapper = buildQueryWrapper(reqDTO);
        List<Account> list = accountMapper.selectList(wrapper);
        List<AccountExcelVO> voList = BeanCopyUtil.copyListProperties(list, AccountExcelVO.class);

        String fileName = "账号列表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replace("+", "%20"));
            EasyExcel.write(response.getOutputStream(), AccountExcelVO.class).sheet("账号列表").doWrite(voList);
        } catch (IOException e) {
            throw new ServiceException("Excel 导出失败");
        }
    }

    /**
     * 构建查询条件
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
