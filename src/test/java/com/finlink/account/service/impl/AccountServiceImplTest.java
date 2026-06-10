package com.finlink.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.finlink.account.domain.dto.AccountQueryReqDTO;
import com.finlink.account.domain.dto.AccountSaveReqDTO;
import com.finlink.account.domain.entity.Account;
import com.finlink.account.domain.vo.AccountPageVO;
import com.finlink.account.domain.vo.AccountVO;
import com.finlink.account.mapper.AccountMapper;
import com.finlink.common.exception.ServiceException;
import com.finlink.flow.domain.entity.TransactionFlow;
import com.finlink.flow.mapper.TransactionFlowMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AccountServiceImpl} 集成测试类。
 * <p>
 * 基于 {@link SpringBootTest} 启动完整 Spring 上下文，对账号的增删改查及关联流水校验进行端到端验证。
 * 每个测试方法前后会自动清理并初始化测试数据，确保用例之间互不干扰。
 * </p>
 *
 * @author 稚名不带撇
 */
@SpringBootTest
@DisplayName("账号服务集成测试")
class AccountServiceImplTest {

    /**
     * 测试账号 ID。
     */
    private static final Long TEST_ACCOUNT_ID = 1L;

    /**
     * 测试账号编号。
     */
    private static final String TEST_ACCOUNT_NO = "TEST_ACC_001";

    /**
     * 测试所属企业名称。
     */
    private static final String TEST_COMPANY = "测试公司";

    /**
     * 测试开户行。
     */
    private static final String TEST_BANK = "测试银行";

    /**
     * 测试数据来源。
     */
    private static final String TEST_DATA_SOURCE = "直联";

    /**
     * 测试币种。
     */
    private static final String TEST_CURRENCY = "CNY";

    /**
     * 账号数据访问层。
     */
    @Autowired
    private AccountMapper accountMapper;

    /**
     * 流水数据访问层，用于构造账号关联流水场景。
     */
    @Autowired
    private TransactionFlowMapper transactionFlowMapper;

    /**
     * 被测账号服务实现。
     */
    @Autowired
    private AccountServiceImpl accountService;

    /**
     * 每个测试方法执行前的初始化操作。
     * <p>
     * 清理历史测试数据后，插入一条基础账号记录，为后续测试提供数据支撑。
     * </p>
     */
    @BeforeEach
    void setUp() {
        // 清理历史测试数据，防止主键或唯一索引冲突
        accountMapper.deleteById(TEST_ACCOUNT_ID);
        accountMapper.delete(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, TEST_ACCOUNT_NO));
        transactionFlowMapper.delete(new LambdaQueryWrapper<TransactionFlow>().eq(TransactionFlow::getOurAccount, TEST_ACCOUNT_NO));

        // 构造基础测试账号并入库
        Account account = new Account();
        account.setId(TEST_ACCOUNT_ID);
        account.setAccountNo(TEST_ACCOUNT_NO);
        account.setCompany(TEST_COMPANY);
        account.setBank(TEST_BANK);
        account.setDataSource(TEST_DATA_SOURCE);
        account.setCurrency(TEST_CURRENCY);
        accountMapper.insert(account);
    }

    /**
     * 每个测试方法执行后的清理操作。
     * <p>
     * 删除由 {@code setUp()} 插入的测试账号及其关联流水，避免对后续测试或数据库造成污染。
     * </p>
     */
    @AfterEach
    void tearDown() {
        // 清理测试账号及关联流水
        accountMapper.deleteById(TEST_ACCOUNT_ID);
        accountMapper.delete(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, TEST_ACCOUNT_NO));
        transactionFlowMapper.delete(new LambdaQueryWrapper<TransactionFlow>().eq(TransactionFlow::getOurAccount, TEST_ACCOUNT_NO));
    }

    /**
     * 测试分页查询账号列表。
     * <p>
     * 期望：返回非空分页对象，总记录数大于等于 1，且记录列表不为空。
     * </p>
     */
    @Test
    @DisplayName("测试分页查询账号列表")
    void testListPage() {
        // 构造分页查询参数
        AccountQueryReqDTO reqDTO = new AccountQueryReqDTO();
        reqDTO.setPageNum(1);
        reqDTO.setPageSize(10);

        // 执行分页查询
        AccountPageVO result = accountService.listPage(reqDTO);

        // 校验分页结果
        assertNotNull(result, "分页结果不应为空");
        assertTrue(result.getTotal() >= 1, "总记录数应至少为 1");
        assertNotNull(result.getRecords(), "记录列表不应为空");
    }

    /**
     * 测试根据 ID 查询账号 —— 账号存在场景。
     * <p>
     * 期望：返回非空 {@link AccountVO}，且账号编号与预期一致。
     * </p>
     */
    @Test
    @DisplayName("测试根据 ID 查询账号 - 账号存在")
    void testGetById_AccountExists() {
        // 执行查询
        AccountVO result = accountService.getById(TEST_ACCOUNT_ID);

        // 校验返回结果
        assertNotNull(result, "查询结果不应为空");
        assertEquals(TEST_ACCOUNT_NO, result.getAccountNo(), "账号号应匹配");
    }

    /**
     * 测试根据 ID 查询账号 —— 账号不存在场景。
     * <p>
     * 期望：抛出 {@link ServiceException}，且异常信息不为空。
     * </p>
     */
    @Test
    @DisplayName("测试根据 ID 查询账号 - 账号不存在")
    void testGetById_AccountNotExists() {
        // 查询一个不存在的 ID，预期抛出业务异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            accountService.getById(99999L);
        });
        assertNotNull(exception.getMessage(), "异常信息不应为空");
    }

    /**
     * 测试新增账号 —— 成功场景。
     * <p>
     * 期望：新增操作不抛出异常，数据库中可查询到对应记录，且字段值正确。
     * </p>
     */
    @Test
    @DisplayName("测试新增账号 - 成功")
    void testAdd_Success() {
        // 构造新增账号请求参数
        AccountSaveReqDTO reqDTO = new AccountSaveReqDTO();
        reqDTO.setAccountNo("TEST_ACC_002");
        reqDTO.setCompany("新公司");
        reqDTO.setBank("新银行");
        reqDTO.setDataSource("手动上传");
        reqDTO.setCurrency("USD");

        // 执行新增并校验无异常
        assertDoesNotThrow(() -> accountService.add(reqDTO), "新增账号不应抛出异常");

        // 从数据库反查并校验字段
        Account saved = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, "TEST_ACC_002"));
        assertNotNull(saved, "数据库中应存在新插入的账号");
        assertEquals("新公司", saved.getCompany(), "所属企业应匹配");

        // 手动清理本次新增的数据
        accountMapper.delete(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, "TEST_ACC_002"));
    }

    /**
     * 测试新增账号 —— 账号已存在场景。
     * <p>
     * 期望：重复插入相同账号编号时抛出 {@link ServiceException}。
     * </p>
     */
    @Test
    @DisplayName("测试新增账号 - 账号已存在")
    void testAdd_AccountExists() {
        // 使用 setUp 中已插入的账号编号，模拟重复新增
        AccountSaveReqDTO reqDTO = new AccountSaveReqDTO();
        reqDTO.setAccountNo(TEST_ACCOUNT_NO);
        reqDTO.setCompany(TEST_COMPANY);
        reqDTO.setBank(TEST_BANK);
        reqDTO.setDataSource(TEST_DATA_SOURCE);
        reqDTO.setCurrency(TEST_CURRENCY);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            accountService.add(reqDTO);
        });
        assertNotNull(exception.getMessage(), "异常信息不应为空");
    }

    /**
     * 测试编辑账号 —— 成功场景。
     * <p>
     * 期望：编辑操作不抛出异常，数据库中对应记录的字段已更新。
     * </p>
     */
    @Test
    @DisplayName("测试编辑账号 - 成功")
    void testEdit_Success() {
        // 构造编辑请求参数，修改公司和账号号
        AccountSaveReqDTO reqDTO = new AccountSaveReqDTO();
        reqDTO.setAccountNo("TEST_ACC_003");
        reqDTO.setCompany("编辑后公司");
        reqDTO.setBank("编辑后银行");
        reqDTO.setDataSource(TEST_DATA_SOURCE);
        reqDTO.setCurrency(TEST_CURRENCY);

        // 执行编辑并校验无异常
        assertDoesNotThrow(() -> accountService.edit(TEST_ACCOUNT_ID, reqDTO), "编辑账号不应抛出异常");

        // 从数据库反查并校验更新结果
        Account updated = accountMapper.selectById(TEST_ACCOUNT_ID);
        assertEquals("编辑后公司", updated.getCompany(), "所属企业应已更新");
        assertEquals("TEST_ACC_003", updated.getAccountNo(), "账号号应已更新");
    }

    /**
     * 测试编辑账号 —— 账号不存在场景。
     * <p>
     * 期望：对不存在的账号执行编辑时抛出 {@link ServiceException}。
     * </p>
     */
    @Test
    @DisplayName("测试编辑账号 - 账号不存在")
    void testEdit_AccountNotExists() {
        // 构造编辑参数，但使用不存在的账号 ID
        AccountSaveReqDTO reqDTO = new AccountSaveReqDTO();
        reqDTO.setAccountNo("TEST_ACC_004");
        reqDTO.setCompany("公司");
        reqDTO.setBank("银行");
        reqDTO.setDataSource(TEST_DATA_SOURCE);
        reqDTO.setCurrency(TEST_CURRENCY);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            accountService.edit(99999L, reqDTO);
        });
        assertNotNull(exception.getMessage(), "异常信息不应为空");
    }

    /**
     * 测试删除账号 —— 成功场景。
     * <p>
     * 期望：删除操作不抛出异常，且数据库中不再存在该账号记录。
     * </p>
     */
    @Test
    @DisplayName("测试删除账号 - 成功")
    void testDelete_Success() {
        // 执行删除并校验无异常
        assertDoesNotThrow(() -> accountService.delete(TEST_ACCOUNT_ID), "删除账号不应抛出异常");

        // 从数据库反查，确认记录已删除
        Account deleted = accountMapper.selectById(TEST_ACCOUNT_ID);
        assertNull(deleted, "删除后数据库中不应存在该账号");
    }

    /**
     * 测试删除账号 —— 账号存在关联流水场景。
     * <p>
     * 期望：当账号下存在关联流水时，删除操作抛出 {@link ServiceException}，阻止删除。
     * </p>
     */
    @Test
    @DisplayName("测试删除账号 - 账号有关联流水")
    void testDelete_HasFlows() {
        // 构造一条关联到测试账号的流水记录
        TransactionFlow flow = new TransactionFlow();
        flow.setId(2L);
        flow.setOurCompany(TEST_COMPANY);
        flow.setOurAccount(TEST_ACCOUNT_NO);
        flow.setOurBank(TEST_BANK);
        flow.setCurrency(TEST_CURRENCY);
        flow.setIncome(BigDecimal.ZERO);
        flow.setExpense(BigDecimal.ZERO);
        flow.setBalance(BigDecimal.ZERO);
        flow.setTradeType("1");
        flow.setCounterpartAccount("");
        flow.setCounterpartBank("");
        flow.setSummary("");
        transactionFlowMapper.insert(flow);

        // 尝试删除账号，预期因存在关联流水而抛出异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            accountService.delete(TEST_ACCOUNT_ID);
        });
        assertNotNull(exception.getMessage(), "异常信息不应为空");

        // 清理本次构造的流水数据
        transactionFlowMapper.deleteById(2L);
    }
}