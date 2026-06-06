package com.finlink.flow.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 流水分页查询请求 DTO
 *
 * @author 稚名不带撇
 */
@Data
public class FlowQueryReqDTO {

    /**
     * 本方企业（模糊匹配）
     */
    private String ourCompany;

    /**
     * 本方账号（精确匹配）
     */
    private String ourAccount;

    /**
     * 币种（从账号表去重获取）
     */
    private String currency;

    /**
     * 交易类型（关联 trade_type.id）
     */
    private String tradeType;

    /**
     * 流水创建时间范围-开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 流水创建时间范围-结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}