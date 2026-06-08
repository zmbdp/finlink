package com.finlink.account.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 账号导出 VO
 * <p>用于导出账号列表到 Excel 文件</p>
 *
 * @author 稚名不带撇
 */
@Data
public class AccountExcelVO {

    /**
     * 账号
     */
    @ExcelProperty("账号")
    private String accountNo;

    /**
     * 所属企业
     */
    @ExcelProperty("所属企业")
    private String company;

    /**
     * 银行
     */
    @ExcelProperty("银行")
    private String bank;

    /**
     * 数据获取方式（直联/手动上传/其他）
     */
    @ExcelProperty("数据获取方式")
    private String dataSource;

    /**
     * 币种
     */
    @ExcelProperty("币种")
    private String currency;
}