USE `finlink_dev`;

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname`    VARCHAR(50)           DEFAULT NULL COMMENT '昵称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态(1正常 0禁用)',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY uk_username(`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统用户表';

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `account_no`  VARCHAR(64)  NOT NULL COMMENT '账号',
    `company`     VARCHAR(128) NOT NULL COMMENT '所属企业',
    `bank`        VARCHAR(128) NOT NULL COMMENT '银行',
    `data_source` VARCHAR(32)  NOT NULL COMMENT '数据获取方式',
    `currency`    VARCHAR(16)  NOT NULL COMMENT '币种',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY uk_account_no(`account_no`),
    KEY           idx_company(`company`),
    KEY           idx_bank(`bank`),
    KEY           idx_currency(`currency`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '账号表';

DROP TABLE IF EXISTS `transaction_flow`;
CREATE TABLE `transaction_flow`
(
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `our_company`         VARCHAR(128)   NOT NULL COMMENT '本方企业',
    `our_account`         VARCHAR(64)    NOT NULL COMMENT '本方账号',
    `our_bank`            VARCHAR(128)   NOT NULL COMMENT '本方银行',
    `counterpart_account` VARCHAR(64)    DEFAULT NULL COMMENT '对方账号',
    `counterpart_bank`    VARCHAR(128)   DEFAULT NULL COMMENT '对方银行',
    `currency`            VARCHAR(16)    NOT NULL COMMENT '币种',
    `income`              DECIMAL(18, 2) DEFAULT 0.00 COMMENT '收入',
    `expense`             DECIMAL(18, 2) DEFAULT 0.00 COMMENT '支出',
    `balance`             DECIMAL(18, 2) NOT NULL COMMENT '余额',
    `summary`             VARCHAR(256)   DEFAULT NULL COMMENT '摘要',
    `trade_type`          VARCHAR(64)    DEFAULT NULL COMMENT '交易类型',
    `create_time`         DATETIME       NOT NULL COMMENT '交易时间',

    KEY                   idx_our_account(`our_account`),
    KEY                   idx_our_company(`our_company`),
    KEY                   idx_currency(`currency`),
    KEY                   idx_trade_type(`trade_type`),
    KEY                   idx_create_time(`create_time`),
    KEY                   idx_account_time(`our_account`, `create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '流水表';

-- 初始密码: admin123 (BCrypt加密)
INSERT INTO sys_user(`username`, `password`, `nickname`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVXg7zxFnbNpvfGmHMQdCFIente', '系统管理员', 1);

INSERT INTO account(`account_no`, `company`, `bank`, `data_source`, `currency`)
VALUES ('622200000001', '腾讯科技', '工商银行', '直联', 'CNY'),
       ('622200000002', '阿里巴巴', '建设银行', '手动上传', 'CNY'),
       ('622200000003', '字节跳动', '招商银行', '直联', 'CNY'),
       ('622200000004', '美团科技', '农业银行', '其他', 'USD'),
       ('622200000005', '京东集团', '交通银行', '直联', 'CNY');
