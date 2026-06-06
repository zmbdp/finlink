package com.finlink.account.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 账号响应 VO
 *
 * @author 稚名不带撇
 */
@Data
public class AccountVO {

    private Long id;

    private String accountNo;

    private String company;

    private String bank;

    private String dataSource;

    private String currency;
}
