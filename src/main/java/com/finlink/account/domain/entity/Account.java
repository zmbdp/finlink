package com.finlink.account.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.finlink.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账号实体类
 *
 * @author 稚名不带撇
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("account")
public class Account extends BaseEntity {

    /**
     * 账号（唯一，关联流水表）
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
     * 数据获取方式（直联 / 手动上传 / 其他）
     */
    private String dataSource;

    /**
     * 币种
     */
    private String currency;
}