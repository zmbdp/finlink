package com.finlink.flow.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.finlink.account.domain.entity.Account;
import com.finlink.account.mapper.AccountMapper;
import com.finlink.common.exception.ServiceException;
import com.finlink.common.utils.BeanCopyUtil;
import com.finlink.flow.domain.dto.FlowQueryReqDTO;
import com.finlink.flow.domain.entity.TradeType;
import com.finlink.flow.domain.entity.TransactionFlow;
import com.finlink.flow.domain.vo.FlowPageVO;
import com.finlink.flow.domain.vo.FlowVO;
import com.finlink.flow.mapper.TradeTypeMapper;
import com.finlink.flow.mapper.TransactionFlowMapper;
import com.finlink.flow.service.IFlowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流水服务实现类
 *
 * @author 稚名不带撇
 */
@Service
public class FlowServiceImpl implements IFlowService {

    @Autowired
    private TransactionFlowMapper transactionFlowMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TradeTypeMapper tradeTypeMapper;

    @Override
    public FlowPageVO listPage(FlowQueryReqDTO reqDTO) {
        Page<TransactionFlow> page = transactionFlowMapper.selectPage(
                new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize()),
                buildQueryWrapper(reqDTO));

        FlowPageVO pageVO = new FlowPageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setRecords(BeanCopyUtil.copyListProperties(page.getRecords(), FlowVO.class));
        return pageVO;
    }

    @Override
    public List<String> listCurrencyOptions() {
        return accountMapper.selectList(
                new LambdaQueryWrapper<Account>()
                        .select(Account::getCurrency)
                        .groupBy(Account::getCurrency)
        ).stream().map(Account::getCurrency).collect(Collectors.toList());
    }

    @Override
    public List<String> listTradeTypeOptions() {
        return tradeTypeMapper.selectList(
                        new LambdaQueryWrapper<TradeType>()
                                .select(TradeType::getTypeName)
                                .isNotNull(TradeType::getTypeName)
                                .orderByAsc(TradeType::getSortOrder)
                )
                .stream()
                .map(TradeType::getTypeName)
                .collect(Collectors.toList());
    }

    @Override
    public void export(FlowQueryReqDTO reqDTO, HttpServletResponse response) {
        List<TransactionFlow> list = transactionFlowMapper.selectList(buildQueryWrapper(reqDTO));
        for (TransactionFlow flow : list) {
            if (flow.getIncome() == null) {
                flow.setIncome(BigDecimal.ZERO);
            }
            if (flow.getExpense() == null) {
                flow.setExpense(BigDecimal.ZERO);
            }
        }
        List<FlowVO> voList = BeanCopyUtil.copyListProperties(list, FlowVO.class);

        String fileName = "流水列表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replace("+", "%20"));
            EasyExcel.write(response.getOutputStream(), FlowVO.class).sheet("流水列表").doWrite(voList);
        } catch (IOException e) {
            throw new ServiceException("Excel 导出失败");
        }
    }

    private LambdaQueryWrapper<TransactionFlow> buildQueryWrapper(FlowQueryReqDTO reqDTO) {
        return new LambdaQueryWrapper<TransactionFlow>()
                .like(StringUtils.isNotBlank(reqDTO.getOurCompany()), TransactionFlow::getOurCompany, reqDTO.getOurCompany())
                .eq(StringUtils.isNotBlank(reqDTO.getOurAccount()), TransactionFlow::getOurAccount, reqDTO.getOurAccount())
                .eq(StringUtils.isNotBlank(reqDTO.getCurrency()), TransactionFlow::getCurrency, reqDTO.getCurrency())
                .eq(StringUtils.isNotBlank(reqDTO.getTradeType()), TransactionFlow::getTradeType, reqDTO.getTradeType())
                .ge(reqDTO.getStartTime() != null, TransactionFlow::getCreateTime, reqDTO.getStartTime())
                .le(reqDTO.getEndTime() != null, TransactionFlow::getCreateTime, reqDTO.getEndTime())
                .orderByDesc(TransactionFlow::getCreateTime);
    }
}
