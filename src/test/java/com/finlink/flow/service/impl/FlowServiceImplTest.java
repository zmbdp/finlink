package com.finlink.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.finlink.account.domain.entity.Account;
import com.finlink.account.mapper.AccountMapper;
import com.finlink.flow.domain.dto.FlowQueryReqDTO;
import com.finlink.flow.domain.entity.TradeType;
import com.finlink.flow.domain.entity.TransactionFlow;
import com.finlink.flow.domain.vo.FlowPageVO;
import com.finlink.flow.mapper.TradeTypeMapper;
import com.finlink.flow.mapper.TransactionFlowMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link FlowServiceImpl} 集成测试类。
 * <p>
 * 使用 {@link SpringBootTest} 启动完整 Spring 上下文，对流水相关的查询、
 * 下拉选项等业务操作进行端到端验证。测试前后会自动清理并初始化账号、
 * 交易类型及流水数据，保证用例之间互不干扰。
 * </p>
 *
 * @author 稚名不带撇
 */
@SpringBootTest
@DisplayName("流水服务集成测试")
class FlowServiceImplTest {

    /**
     * 测试流水记录 ID。
     */
    private static final Long TEST_FLOW_ID = 1L;

    /**
     * 测试账号 ID。
     */
    private static final Long TEST_ACCOUNT_ID = 1L;

    /**
     * 测试交易类型 ID。
     */
    private static final Long TEST_TRADE_TYPE_ID = 1L;

    /**
     * 测试账号编号。
     */
    private static final String TEST_ACCOUNT_NO = "TEST_FLOW_ACC_001";

    /**
     * 测试所属企业名称。
     */
    private static final String TEST_COMPANY = "测试流水公司";


    /**
     * 流水数据访问层。
     */
    @Autowired
    private TransactionFlowMapper transactionFlowMapper;

    /**
     * 账号数据访问层。
     */
    @Autowired
    private AccountMapper accountMapper;

    /**
     * 交易类型数据访问层。
     */
    @Autowired
    private TradeTypeMapper tradeTypeMapper;

    /**
     * 被测流水服务实现。
     */
    @Autowired
    private FlowServiceImpl flowService;

    /**
     * 每个测试方法执行前的初始化操作。
     * <p>
     * 清理历史测试数据后，依次插入账号、交易类型及流水记录，为后续测试提供基础数据。
     * </p>
     */
    @BeforeEach
    void setUp() {
        // 清理历史测试数据，防止主键或唯一索引冲突
        transactionFlowMapper.deleteById(TEST_FLOW_ID);
        accountMapper.deleteById(TEST_ACCOUNT_ID);
        tradeTypeMapper.deleteById(TEST_TRADE_TYPE_ID);
        accountMapper.delete(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, TEST_ACCOUNT_NO));
        transactionFlowMapper.delete(new LambdaQueryWrapper<TransactionFlow>().eq(TransactionFlow::getOurAccount, TEST_ACCOUNT_NO));

        // 构造基础测试账号并入库
        Account account = new Account();
        account.setId(TEST_ACCOUNT_ID);
        account.setAccountNo(TEST_ACCOUNT_NO);
        account.setCompany(TEST_COMPANY);
        account.setBank("测试银行");
        account.setDataSource("直联");
        account.setCurrency("CNY");
        accountMapper.insert(account);

        // 构造基础测试交易类型并入库
        TradeType tradeType = new TradeType();
        tradeType.setId(TEST_TRADE_TYPE_ID);
        tradeType.setTypeName("收入");
        tradeType.setSortOrder(1);
        tradeTypeMapper.insert(tradeType);

        // 构造基础测试流水记录并入库
        TransactionFlow flow = new TransactionFlow();
        flow.setId(TEST_FLOW_ID);
        flow.setOurCompany(TEST_COMPANY);
        flow.setOurAccount(TEST_ACCOUNT_NO);
        flow.setOurBank("测试银行");
        flow.setCurrency("CNY");
        flow.setIncome(new BigDecimal("1000.00"));
        flow.setExpense(BigDecimal.ZERO);
        flow.setBalance(new BigDecimal("1000.00"));
        flow.setTradeType(String.valueOf(TEST_TRADE_TYPE_ID));
        flow.setCounterpartAccount("");
        flow.setCounterpartBank("");
        flow.setSummary("");
        transactionFlowMapper.insert(flow);
    }

    /**
     * 每个测试方法执行后的清理操作。
     * <p>
     * 删除由 {@code setUp()} 插入的测试数据，避免对后续测试或数据库造成污染。
     * </p>
     */
    @AfterEach
    void tearDown() {
        // 清理测试流水、账号及交易类型数据
        transactionFlowMapper.deleteById(TEST_FLOW_ID);
        accountMapper.deleteById(TEST_ACCOUNT_ID);
        tradeTypeMapper.deleteById(TEST_TRADE_TYPE_ID);
        accountMapper.delete(new LambdaQueryWrapper<Account>().eq(Account::getAccountNo, TEST_ACCOUNT_NO));
        transactionFlowMapper.delete(new LambdaQueryWrapper<TransactionFlow>().eq(TransactionFlow::getOurAccount, TEST_ACCOUNT_NO));
    }

    /**
     * 测试分页查询流水列表。
     * <p>
     * 期望：返回非空分页对象，总记录数大于等于 1，且记录列表不为空。
     * </p>
     */
    @Test
    @DisplayName("测试分页查询流水列表")
    void testListPage() {
        // 构造分页查询参数
        FlowQueryReqDTO reqDTO = new FlowQueryReqDTO();
        reqDTO.setPageNum(1);
        reqDTO.setPageSize(10);

        // 执行分页查询
        FlowPageVO result = flowService.listPage(reqDTO);

        // 校验分页结果
        assertNotNull(result, "分页结果不应为空");
        assertTrue(result.getTotal() >= 1, "总记录数应至少为 1");
        assertNotNull(result.getRecords(), "记录列表不应为空");
    }

    /**
     * 测试获取币种下拉选项。
     * <p>
     * 期望：返回非空列表，且包含常用币种 "CNY"。
     * </p>
     */
    @Test
    @DisplayName("测试获取币种下拉选项")
    void testListCurrencyOptions() {
        // 直接调用服务层获取币种下拉列表
        List<String> result = flowService.listCurrencyOptions();

        // 校验返回结果包含人民币币种
        assertNotNull(result, "币种列表不应为空");
        assertTrue(result.contains("CNY"), "币种列表应包含 CNY");
    }

    /**
     * 测试获取交易类型下拉选项。
     * <p>
     * 期望：返回非空列表，且包含已初始化的交易类型 "收入"。
     * </p>
     */
    @Test
    @DisplayName("测试获取交易类型下拉选项")
    void testListTradeTypeOptions() {
        // 直接调用服务层获取交易类型下拉列表
        List<String> result = flowService.listTradeTypeOptions();

        // 校验返回结果包含已入库的交易类型
        assertNotNull(result, "交易类型列表不应为空");
        assertTrue(result.contains("收入"), "交易类型列表应包含 收入");
    }
}

