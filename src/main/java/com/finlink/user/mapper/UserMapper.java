package com.finlink.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.finlink.user.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 * <p>提供 sys_user 表的 CRUD 操作</p>
 *
 * @author 稚名不带撇
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}