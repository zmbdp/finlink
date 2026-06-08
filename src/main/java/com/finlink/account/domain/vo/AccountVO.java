package com.finlink.account.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 账号响应 VO
 * <p>用于返回账号详情和列表数据</p>
 *
 * @author 稚名不带撇
 */
@Data
public class AccountVO {

    /**
     * 账号 ID
     */
    private Long id;

    /**
     * 账号
     */
    private String accountNo;

    /**
     * 所属企业
     */
    private String company;

    /**
     * 银行
     */
    private String bank;

    /**
     * 数据获取方式（直联/手动上传/其他）
     */
    private String dataSource;

    /**
     * 币种
     */
    private String currency;
}