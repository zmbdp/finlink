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
    `counterpart_account` VARCHAR(64)    NOT NULL COMMENT '对方账号',
    `counterpart_bank`    VARCHAR(128)   NOT NULL COMMENT '对方银行',
    `currency`            VARCHAR(16)    NOT NULL COMMENT '币种',
    `income`              DECIMAL(18, 2) DEFAULT 0.00 COMMENT '收入',
    `expense`             DECIMAL(18, 2) DEFAULT 0.00 COMMENT '支出',
    `balance`             DECIMAL(18, 2) NOT NULL COMMENT '余额',
    `summary`             VARCHAR(256)   DEFAULT NULL COMMENT '摘要',
    `trade_type`          VARCHAR(64)    DEFAULT NULL COMMENT '交易类型（关联 trade_type.id）',
    `create_time`         DATETIME       NOT NULL COMMENT '交易时间',

    KEY                   idx_our_account(`our_account`),
    KEY                   idx_our_company(`our_company`),
    KEY                   idx_currency(`currency`),
    KEY                   idx_trade_type(`trade_type`),
    KEY                   idx_create_time(`create_time`),
    KEY                   idx_account_time(`our_account`, `create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '流水表';

DROP TABLE IF EXISTS `trade_type`;
CREATE TABLE `trade_type`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `type_name`   VARCHAR(50) COMMENT '交易类型名称',
    `sort_order`  INT     DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    `is_deleted`  TINYINT DEFAULT 0 COMMENT '是否删除(0否 1是)',
    INDEX         `idx_sort_order` (`sort_order`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '交易类型表';

-- 初始密码: admin123 (BCrypt加密)
INSERT INTO sys_user(`username`, `password`, `nickname`, `status`)
VALUES ('admin', '$2a$10$s1WjTHpqgbecsliXY/P7CulfFXL1I1rEP8psDkc3dTdn5Dstv0im2', '系统管理员', 1);

INSERT INTO account(`account_no`, `company`, `bank`, `data_source`, `currency`)
VALUES ('622200000001', '腾讯科技', '工商银行', '直联', 'CNY'),
       ('622200000002', '阿里巴巴', '建设银行', '手动上传', 'CNY'),
       ('622200000003', '字节跳动', '招商银行', '直联', 'CNY'),
       ('622200000004', '美团科技', '农业银行', '其他', 'USD'),
       ('622200000005', '京东集团', '交通银行', '直联', 'CNY');

INSERT INTO trade_type(`type_name`, `sort_order`, `create_time`, `update_time`)
VALUES ('转账', 1, NOW(), NOW()),
       ('收款', 2, NOW(), NOW()),
       ('付款', 3, NOW(), NOW()),
       ('退款', 4, NOW(), NOW()),
       ('利息', 5, NOW(), NOW()),
       ('手续费', 6, NOW(), NOW()),
       ('工资', 7, NOW(), NOW()),
       ('报销', 8, NOW(), NOW());

INSERT INTO transaction_flow(`our_company`, `our_account`, `our_bank`, `counterpart_account`, `counterpart_bank`,
                             `currency`, `income`, `expense`, `balance`, `summary`, `trade_type`, `create_time`)
VALUES ('腾讯科技', '622200000001', '工商银行', '6227001234567890', '建设银行', 'CNY', 100000.00, 0.00, 500000.00,
        '项目收款-2026Q1', '收款', '2026-06-01 09:30:00'),
       ('腾讯科技', '622200000001', '工商银行', '6228009876543210', '招商银行', 'CNY', 0.00, 25000.00, 475000.00,
        '支付服务器托管费', '付款', '2026-06-02 14:20:00'),
       ('腾讯科技', '622200000001', '工商银行', '6227000000000000', '工商银行', 'CNY', 0.00, 1500.00, 473500.00,
        '银行手续费', '手续费', '2026-06-03 08:00:00'),
       ('腾讯科技', '622200000001', '工商银行', '622200000001', '工商银行', 'CNY', 200000.00, 0.00, 673500.00,
        '理财产品赎回', '转账', '2026-06-04 10:15:00'),
       ('腾讯科技', '622200000001', '工商银行', '6217005566778899', '中国银行', 'CNY', 0.00, 80000.00, 593500.00,
        '支付供应商货款', '付款', '2026-06-05 16:45:00'),

       ('阿里巴巴', '622200000002', '建设银行', '6227002233445566', '建设银行', 'CNY', 500000.00, 0.00, 1200000.00,
        '电商平台结算款', '收款', '2026-06-01 11:00:00'),
       ('阿里巴巴', '622200000002', '建设银行', '6230007788990011', '农业银行', 'CNY', 0.00, 120000.00, 1080000.00,
        '支付员工工资', '工资', '2026-06-03 09:00:00'),
       ('阿里巴巴', '622200000002', '建设银行', '6222000000000001', '建设银行', 'CNY', 5000.00, 0.00, 1085000.00,
        '活期利息结算', '利息', '2026-06-04 03:00:00'),
       ('阿里巴巴', '622200000002', '建设银行', '6218003344556677', '招商银行', 'CNY', 0.00, 30000.00, 1055000.00,
        '报销差旅费', '报销', '2026-06-05 13:30:00'),

       ('字节跳动', '622200000003', '招商银行', '6229001122334455', '中信银行', 'CNY', 300000.00, 0.00, 800000.00,
        '广告业务收入', '收款', '2026-06-02 10:00:00'),
       ('字节跳动', '622200000003', '招商银行', '6216009988776655', '交通银行', 'CNY', 0.00, 50000.00, 750000.00,
        '服务器采购款', '付款', '2026-06-04 15:00:00'),
       ('字节跳动', '622200000003', '招商银行', '622200000003', '招商银行', 'CNY', 100000.00, 0.00, 850000.00,
        '内部转账', '转账', '2026-06-06 08:30:00'),

       ('美团科技', '622200000004', '农业银行', '6215003344556677', '工商银行', 'USD', 50000.00, 0.00, 200000.00,
        '海外平台结算', '收款', '2026-06-01 16:00:00'),
       ('美团科技', '622200000004', '农业银行', '6215003344556677', '工商银行', 'USD', 0.00, 0.00, 199850.00,
        '跨境汇款手续费', '手续费', '2026-06-02 09:00:00'),
       ('美团科技', '622200000004', '农业银行', '6213009988776655', '中国银行', 'USD', 0.00, 30000.00, 169850.00,
        '支付海外供应商', '付款', '2026-06-05 11:00:00'),

       ('京东集团', '622200000005', '交通银行', '6227005566778899', '建设银行', 'CNY', 200000.00, 0.00, 600000.00,
        '客户退款返回', '退款', '2026-06-03 14:00:00'),
       ('京东集团', '622200000005', '交通银行', '6229007788990011', '中信银行', 'CNY', 0.00, 60000.00, 540000.00,
        '支付物流费用', '付款', '2026-06-04 17:00:00'),
       ('京东集团', '622200000005', '交通银行', '6222000000000002', '交通银行', 'CNY', 3500.00, 0.00, 543500.00,
        '季度利息结算', '利息', '2026-06-06 02:00:00');