package com.finlink.account.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 账号分页响应 VO
 *
 * @author 稚名不带撇
 */
@Data
public class AccountPageVO {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<AccountVO> records;
}
