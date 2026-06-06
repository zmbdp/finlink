package com.finlink.account.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 账号新增/编辑请求 DTO
 *
 * @author 稚名不带撇
 */
@Data
public class AccountSaveReqDTO {

    /**
     * 账号（唯一标识）
     */
    @NotBlank(message = "账号不能为空")
    private String accountNo;

    /**
     * 所属企业
     */
    @NotBlank(message = "所属企业不能为空")
    private String company;

    /**
     * 银行
     */
    @NotBlank(message = "银行不能为空")
    private String bank;

    /**
     * 数据获取方式（直联 / 手动上传 / 其他）
     */
    @NotBlank(message = "数据获取方式不能为空")
    private String dataSource;

    /**
     * 币种
     */
    @NotBlank(message = "币种不能为空")
    private String currency;
}
