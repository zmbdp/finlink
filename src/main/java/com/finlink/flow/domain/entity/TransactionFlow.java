package com.finlink.flow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.finlink.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 流水实体类
 *
 * @author 稚名不带撇
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("transaction_flow")
public class TransactionFlow extends BaseEntity {

    /**
     * 本方企业
     */
    private String ourCompany;

    /**
     * 本方账号（关联 account.account_no）
     */
    private String ourAccount;

    /**
     * 本方银行
     */
    private String ourBank;

    /**
     * 对方账号
     */
    private String counterpartAccount;

    /**
     * 对方银行
     */
    private String counterpartBank;

    /**
     * 币种
     */
    private String currency;

    /**
     * 收入
     */
    private BigDecimal income;

    /**
     * 支出
     */
    private BigDecimal expense;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 交易类型（关联 trade_type.id）
     */
    private String tradeType;

    /**
     * 覆盖父类的 updateTime，忽略映射
     */
    @TableField(exist = false)
    private LocalDateTime updateTime;
}