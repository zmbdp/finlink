package com.finlink.account.domain.dto;

import lombok.Data;

/**
 * 账号分页查询请求 DTO
 *
 * @author 稚名不带撇
 */
@Data
public class AccountQueryReqDTO {

    /**
     * 账号（模糊匹配）
     */
    private String accountNo;

    /**
     * 所属企业（模糊匹配）
     */
    private String company;

    /**
     * 银行（模糊匹配）
     */
    private String bank;

    /**
     * 数据获取方式（精确匹配）
     */
    private String dataSource;

    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}
