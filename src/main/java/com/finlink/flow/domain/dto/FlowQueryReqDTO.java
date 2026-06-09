package com.finlink.flow.domain.dto;

import com.finlink.common.constants.CommonConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 流水分页查询请求 DTO
 *
 * @author 稚名不带撇
 */
@Data
@ApiModel("流水分页查询请求参数")
public class FlowQueryReqDTO {

    /**
     * 本方企业（模糊匹配）
     */
    @ApiModelProperty(value = "本方企业（模糊匹配）", example = "腾讯科技")
    private String ourCompany;

    /**
     * 本方账号（精确匹配）
     */
    @ApiModelProperty(value = "本方账号（精确匹配）", example = "622200000001")
    private String ourAccount;

    /**
     * 币种（从账号表去重获取）
     */
    @ApiModelProperty(value = "币种", example = "CNY")
    private String currency;

    /**
     * 交易类型（关联 trade_type.id）
     */
    @ApiModelProperty(value = "交易类型", example = "转账")
    private String tradeType;

    /**
     * 流水创建时间范围-开始
     */
    @ApiModelProperty(value = "流水创建时间范围-开始", example = "2026-06-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 流水创建时间范围-结束
     */
    @ApiModelProperty(value = "流水创建时间范围-结束", example = "2026-06-06 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 当前页码（从1开始）
     */
    @ApiModelProperty(value = "当前页码（从1开始）", example = "1")
    private Integer pageNum = CommonConstants.DEFAULT_PAGE_NUM;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize = CommonConstants.DEFAULT_PAGE_SIZE;
}