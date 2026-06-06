package com.finlink.flow.service;

import com.finlink.flow.domain.dto.FlowQueryReqDTO;
import com.finlink.flow.domain.vo.FlowPageVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流水服务接口
 *
 * @author 稚名不带撇
 */
public interface IFlowService {

    /**
     * 分页查询流水列表（LEFT JOIN 账号表获取交易类型）
     *
     * @param reqDTO 查询条件
     * @return 分页结果
     */
    FlowPageVO listPage(FlowQueryReqDTO reqDTO);

    /**
     * 查询币种下拉选项（从账号表去重获取）
     *
     * @return 币种列表
     */
    List<String> listCurrencyOptions();

    /**
     * 查询交易类型下拉选项（从账号表关联获取）
     *
     * @return 交易类型列表
     */
    List<String> listTradeTypeOptions();

    /**
     * 导出流水列表为 Excel
     *
     * @param reqDTO   查询条件
     * @param response HTTP 响应
     */
    void export(FlowQueryReqDTO reqDTO, HttpServletResponse response);
}
