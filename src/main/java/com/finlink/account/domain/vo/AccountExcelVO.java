package com.finlink.account.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 账号导出 VO
 *
 * @author 稚名不带撇
 */
@Data
public class AccountExcelVO {

    @ExcelProperty("账号")
    private String accountNo;

    @ExcelProperty("所属企业")
    private String company;

    @ExcelProperty("银行")
    private String bank;

    @ExcelProperty("数据获取方式")
    private String dataSource;

    @ExcelProperty("币种")
    private String currency;
}
