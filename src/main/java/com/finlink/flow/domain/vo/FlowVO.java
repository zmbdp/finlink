package com.finlink.flow.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 流水响应 VO
 * <p>用于返回流水详情和列表数据，同时用于 Excel 导出
 *
 * @author 稚名不带撇
 */
@Data
public class FlowVO {

    /**
     * 本方企业
     */
    @ExcelProperty("本方企业")
    private String ourCompany;

    /**
     * 本方账号
     */
    @ExcelProperty("本方账号")
    private String ourAccount;

    /**
     * 本方银行
     */
    @ExcelProperty("本方银行")
    private String ourBank;

    /**
     * 对方账号
     */
    @ExcelProperty("对方账号")
    private String counterpartAccount;

    /**
     * 对方银行
     */
    @ExcelProperty("对方银行")
    private String counterpartBank;

    /**
     * 币种
     */
    @ExcelProperty("币种")
    private String currency;

    /**
     * 收入金额
     */
    @NumberFormat("#,##0.00")
    @ColumnWidth(16)
    @ExcelProperty("收入")
    private BigDecimal income;

    /**
     * 支出金额
     */
    @NumberFormat("#,##0.00")
    @ColumnWidth(16)
    @ExcelProperty("支出")
    private BigDecimal expense;

    /**
     * 余额
     */
    @NumberFormat("#,##0.00")
    @ColumnWidth(16)
    @ExcelProperty("余额")
    private BigDecimal balance;

    /**
     * 摘要
     */
    @ExcelProperty("摘要")
    private String summary;

    /**
     * 交易类型（显示名称）
     */
    @ExcelProperty("交易类型")
    private String tradeType;

    /**
     * 交易时间（创建时间）
     */
    @ColumnWidth(22)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("交易时间")
    private LocalDateTime createTime;
}