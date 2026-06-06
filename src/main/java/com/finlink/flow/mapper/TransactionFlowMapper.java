package com.finlink.flow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.finlink.flow.domain.entity.TransactionFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 流水 Mapper
 *
 * @author 稚名不带撇
 */
@Mapper
public interface TransactionFlowMapper extends BaseMapper<TransactionFlow> {

    /**
     * 同步更新流水表中的账号信息
     * <p>当账号表中的账号被修改时，同步更新流水表中所有关联记录</p>
     *
     * @param oldAccountNo 旧账号
     * @param newAccountNo 新账号
     * @param newCompany   新企业名称
     * @param newBank      新银行名称
     * @param newCurrency  新币种
     * @return 更新的记录数
     */
    int syncUpdateByAccountNo(@Param("oldAccountNo") String oldAccountNo,
                              @Param("newAccountNo") String newAccountNo,
                              @Param("newCompany") String newCompany,
                              @Param("newBank") String newBank,
                              @Param("newCurrency") String newCurrency);

    /**
     * 同步更新流水表中的对方账号信息
     * <p>当账号表中的账号被修改时，同步更新流水表中所有以该账号为对方账号的记录</p>
     *
     * @param oldAccountNo 旧对方账号
     * @param newAccountNo 新对方账号
     * @param newBank      新对方银行
     * @return 更新的记录数
     */
    int syncUpdateByCounterpartAccountNo(@Param("oldAccountNo") String oldAccountNo,
                                         @Param("newAccountNo") String newAccountNo,
                                         @Param("newBank") String newBank);

    /**
     * 检查账号是否有关联的流水记录
     *
     * @param accountNo 账号
     * @return 关联的流水记录数
     */
    int countByAccountNo(@Param("accountNo") String accountNo);
}

