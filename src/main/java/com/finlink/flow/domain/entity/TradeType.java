package com.finlink.flow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.finlink.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 交易类型字典实体类
 *
 * @author 稚名不带撇
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("trade_type")
public class TradeType extends BaseEntity {

    /**
     * 交易类型名称
     */
    private String typeName;

    /**
     * 排序
     */
    private Integer sortOrder;
}