package com.finlink.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.finlink.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 * <p>对应数据库 sys_user 表</p>
 *
 * @author 稚名不带撇
 */
@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}