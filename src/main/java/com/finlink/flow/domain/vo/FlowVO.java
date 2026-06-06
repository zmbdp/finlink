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
 *
 * @author 稚名不带撇
 */
@Data
public class FlowVO {

    @ExcelProperty("本方企业")
    private String ourCompany;

    @ExcelProperty("本方账号")
    private String ourAccount;

    @ExcelProperty("本方银行")
    private String ourBank;

    @ExcelProperty("对方账号")
    private String counterpartAccount;

    @ExcelProperty("对方银行")
    private String counterpartBank;

    @ExcelProperty("币种")
    private String currency;

    @NumberFormat("#,##0.00")
    @ColumnWidth(16)
    @ExcelProperty("收入")
    private BigDecimal income;

    @NumberFormat("#,##0.00")
    @ColumnWidth(16)
    @ExcelProperty("支出")
    private BigDecimal expense;

    @NumberFormat("#,##0.00")
    @ColumnWidth(16)
    @ExcelProperty("余额")
    private BigDecimal balance;

    @ExcelProperty("摘要")
    private String summary;

    @ExcelProperty("交易类型")
    private String tradeType;

    @ColumnWidth(22)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("交易时间")
    private LocalDateTime createTime;
}