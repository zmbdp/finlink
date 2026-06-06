package com.finlink.flow.controller;

import com.finlink.common.domain.Result;
import com.finlink.flow.domain.dto.FlowQueryReqDTO;
import com.finlink.flow.domain.vo.FlowPageVO;
import com.finlink.flow.service.IFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流水管理控制器
 * <p>只读页面：只提供查询和导出功能，不可新增、编辑、删除</p>
 *
 * @author 稚名不带撇
 */
@RestController
@RequestMapping("/api/flow")
public class FlowController {

    @Autowired
    private IFlowService flowService;

    /**
     * 分页查询流水列表
     * <p>通过 LEFT JOIN 账号表获取交易类型</p>
     *
     * @param reqDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<FlowPageVO> list(FlowQueryReqDTO reqDTO) {
        return Result.success(flowService.listPage(reqDTO));
    }

    /**
     * 查询币种下拉选项
     * <p>从账号表币种去重获取</p>
     *
     * @return 币种列表
     */
    @GetMapping("/currencyOptions")
    public Result<List<String>> listCurrencyOptions() {
        return Result.success(flowService.listCurrencyOptions());
    }

    /**
     * 查询交易类型下拉选项
     * <p>从账号表关联获取（即 data_source 字段）</p>
     *
     * @return 交易类型列表
     */
    @GetMapping("/tradeTypeOptions")
    public Result<List<String>> listTradeTypeOptions() {
        return Result.success(flowService.listTradeTypeOptions());
    }

    /**
     * 导出流水列表为 Excel
     * <p>文件名格式：流水列表_YYYYMMDD.xlsx</p>
     *
     * @param reqDTO   查询条件
     * @param response HTTP 响应
     */
    @GetMapping("/export")
    public void export(FlowQueryReqDTO reqDTO, HttpServletResponse response) {
        flowService.export(reqDTO, response);
    }
}