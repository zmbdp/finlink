package com.finlink.flow.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.finlink.account.domain.entity.Account;
import com.finlink.account.mapper.AccountMapper;
import com.finlink.common.constants.CommonConstants;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流水服务实现类
 * <p>实现流水的分页查询、导出、下拉选项查询等功能</p>
 *
 * @author 稚名不带撇
 */
@Service
public class FlowServiceImpl implements IFlowService {

    /**
     * 流水数据访问接口
     */
    @Autowired
    private TransactionFlowMapper transactionFlowMapper;

    /**
     * 账号数据访问接口，用于获取账号和币种下拉选项
     */
    @Autowired
    private AccountMapper accountMapper;

    /**
     * 交易类型数据访问接口，用于获取交易类型下拉选项和名称映射
     */
    @Autowired
    private TradeTypeMapper tradeTypeMapper;

    /**
     * 分页查询流水列表
     * <p>根据查询条件构建查询 wrapper，执行分页查询，转换交易类型 ID 为交易类型名称
     *
     * @param reqDTO 查询条件
     * @return 流水分页结果
     */
    @Override
    public FlowPageVO listPage(FlowQueryReqDTO reqDTO) {
        // 执行分页查询获取原始流水记录
        Page<TransactionFlow> page = transactionFlowMapper.selectPage(
                new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize()),
                buildQueryWrapper(reqDTO));

        // 获取交易类型 ID 与名称的映射，用于后续转换
        Map<String, String> tradeTypeNameMap = getTradeTypeNameMap();

        // 将流水实体转换为 VO，并把交易类型 ID 替换为可读名称
        List<FlowVO> voList = page.getRecords().stream().map(flow -> {
            FlowVO vo = BeanCopyUtil.copyProperties(flow, FlowVO.class);
            if (StringUtils.isNotBlank(flow.getTradeType())) {
                vo.setTradeType(tradeTypeNameMap.getOrDefault(flow.getTradeType(), flow.getTradeType()));
            }
            return vo;
        }).collect(Collectors.toList());

        // 组装分页结果
        FlowPageVO pageVO = new FlowPageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setRecords(voList);
        return pageVO;
    }

    /**
     * 获取币种下拉选项
     * <p>查询所有账号的币种并去重，用于筛选条件的币种选择
     *
     * @return 币种列表
     */
    @Override
    public List<String> listCurrencyOptions() {
        // 查询所有账号的币种并按币种去重，返回币种列表供下拉选择
        return accountMapper.selectList(
                new LambdaQueryWrapper<Account>()
                        .select(Account::getCurrency)
                        .groupBy(Account::getCurrency)
        ).stream().map(Account::getCurrency).collect(Collectors.toList());
    }

    /**
     * 获取交易类型下拉选项
     * <p>查询所有交易类型列表，按排序字段升序排列，用于筛选条件的交易类型选择
     *
     * @return 交易类型名称列表
     */
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

    /**
     * 导出流水列表为 Excel
     * <p>根据查询条件查询数据，转换交易类型 ID 为交易类型名称，将空金额设为 0，导出为 Excel 文件，文件名格式：流水列表_YYYYMMDD.xlsx
     *
     * @param reqDTO   查询条件
     * @param response HTTP 响应
     * @throws ServiceException 如果 Excel 导出失败
     */
    @Override
    public void export(FlowQueryReqDTO reqDTO, HttpServletResponse response) {
        // 查询符合条件的流水数据
        List<TransactionFlow> list = transactionFlowMapper.selectList(buildQueryWrapper(reqDTO));
        Map<String, String> tradeTypeNameMap = getTradeTypeNameMap();

        // 转换为 VO，处理空金额并替换交易类型 ID 为名称
        List<FlowVO> voList = list.stream().map(flow -> {
            FlowVO vo = BeanCopyUtil.copyProperties(flow, FlowVO.class);
            if (vo.getIncome() == null) {
                vo.setIncome(BigDecimal.ZERO);
            }
            if (vo.getExpense() == null) {
                vo.setExpense(BigDecimal.ZERO);
            }
            if (StringUtils.isNotBlank(flow.getTradeType())) {
                vo.setTradeType(tradeTypeNameMap.getOrDefault(flow.getTradeType(), flow.getTradeType()));
            }
            return vo;
        }).collect(Collectors.toList());

        // 组装文件名并设置响应头，触发浏览器下载
        String fileName = CommonConstants.EXCEL_FILE_NAME_FLOW + LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT_SHORT)) + CommonConstants.EXCEL_FILE_SUFFIX;
        response.setContentType(CommonConstants.EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(CommonConstants.UTF8);
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, CommonConstants.UTF8).replace("+", "%20"));
            EasyExcel.write(response.getOutputStream(), FlowVO.class).sheet(CommonConstants.EXCEL_SHEET_NAME_FLOW).doWrite(voList);
        } catch (IOException e) {
            throw new ServiceException(CommonConstants.ERROR_MSG_EXCEL_EXPORT_FAILED);
        }
    }

    /**
     * 构建流水查询条件 Wrapper。
     * <p>将前端传入的交易类型名称转换为 ID，支持模糊匹配和时间段筛选</p>
     *
     * @param reqDTO 前端传入的查询参数
     * @return 组装后的 MyBatis-Plus 查询 Wrapper
     */
    private LambdaQueryWrapper<TransactionFlow> buildQueryWrapper(FlowQueryReqDTO reqDTO) {
        // 若传入了交易类型名称，先转换为其对应的 ID
        String tradeTypeId = null;
        if (StringUtils.isNotBlank(reqDTO.getTradeType())) {
            tradeTypeId = getTradeTypeIdByName(reqDTO.getTradeType());
        }

        return new LambdaQueryWrapper<TransactionFlow>()
                .like(StringUtils.isNotBlank(reqDTO.getOurCompany()), TransactionFlow::getOurCompany, reqDTO.getOurCompany())
                .eq(StringUtils.isNotBlank(reqDTO.getOurAccount()), TransactionFlow::getOurAccount, reqDTO.getOurAccount())
                .eq(StringUtils.isNotBlank(reqDTO.getCurrency()), TransactionFlow::getCurrency, reqDTO.getCurrency())
                .eq(StringUtils.isNotBlank(tradeTypeId), TransactionFlow::getTradeType, tradeTypeId)
                .ge(reqDTO.getStartTime() != null, TransactionFlow::getCreateTime, reqDTO.getStartTime())
                .le(reqDTO.getEndTime() != null, TransactionFlow::getCreateTime, reqDTO.getEndTime())
                .orderByDesc(TransactionFlow::getCreateTime);
    }

    /**
     * 获取交易类型 ID 到名称的映射表，用于列表展示时替换 ID 为可读名称。
     *
     * @return 交易类型 ID 与名称的映射
     */
    private Map<String, String> getTradeTypeNameMap() {
        List<TradeType> tradeTypes = tradeTypeMapper.selectList(
                new LambdaQueryWrapper<TradeType>()
                        .select(TradeType::getId, TradeType::getTypeName)
        );
        return tradeTypes.stream()
                .filter(tt -> tt.getId() != null)
                .collect(Collectors.toMap(tt -> String.valueOf(tt.getId()), TradeType::getTypeName));
    }

    /**
     * 根据交易类型名称获取对应的交易类型 ID。
     *
     * @param typeName 交易类型名称
     * @return 交易类型 ID，未找到则返回 null
     */
    private String getTradeTypeIdByName(String typeName) {
        TradeType tradeType = tradeTypeMapper.selectOne(
                new LambdaQueryWrapper<TradeType>()
                        .eq(TradeType::getTypeName, typeName)
                        .last("LIMIT 1")
        );
        return tradeType != null ? String.valueOf(tradeType.getId()) : null;
    }
}