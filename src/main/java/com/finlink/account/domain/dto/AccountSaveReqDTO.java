package com.finlink.account.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 账号新增/编辑请求 DTO
 *
 * @author 稚名不带撇
 */
@Data
@ApiModel("账号新增/编辑请求参数")
public class AccountSaveReqDTO {

    /**
     * 账号（唯一标识）
     */
    @ApiModelProperty(value = "账号（唯一标识）", required = true, example = "622200000001")
    @NotBlank(message = "账号不能为空")
    private String accountNo;

    /**
     * 所属企业
     */
    @ApiModelProperty(value = "所属企业", required = true, example = "腾讯科技")
    @NotBlank(message = "所属企业不能为空")
    private String company;

    /**
     * 银行
     */
    @ApiModelProperty(value = "银行", required = true, example = "工商银行")
    @NotBlank(message = "银行不能为空")
    private String bank;

    /**
     * 数据获取方式（直联 / 手动上传 / 其他）
     */
    @ApiModelProperty(value = "数据获取方式", required = true, example = "直联")
    @NotBlank(message = "数据获取方式不能为空")
    private String dataSource;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", required = true, example = "CNY")
    @NotBlank(message = "币种不能为空")
    private String currency;
}