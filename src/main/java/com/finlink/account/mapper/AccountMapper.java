package com.finlink.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.finlink.account.domain.entity.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账号 Mapper
 *
 * @author 稚名不带撇
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}