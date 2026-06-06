package com.finlink.flow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.finlink.flow.domain.entity.TradeType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易类型字典数据访问接口
 *
 * @author 稚名不带撇
 */
@Mapper
public interface TradeTypeMapper extends BaseMapper<TradeType> {
}