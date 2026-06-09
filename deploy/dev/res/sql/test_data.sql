USE `finlink_dev`;

-- =============================================
-- 测试数据准备
-- =============================================

-- 1. 插入测试用户（默认密码都是 admin123）
-- BCrypt 加密后的密码：$2a$10$s1WjTHpqgbecsliXY/P7CulfFXL1I1rEP8psDkc3dTdn5Dstv0im2
INSERT INTO sys_user (username, password, nickname, status)
VALUES ('test001', '$2a$10$s1WjTHpqgbecsliXY/P7CulfFXL1I1rEP8psDkc3dTdn5Dstv0im2', '测试账号001', 1),
       ('test002', '$2a$10$s1WjTHpqgbecsliXY/P7CulfFXL1I1rEP8psDkc3dTdn5Dstv0im2', '测试账号002', 1);

-- 2. 插入测试账号（这是要演示修改的账号！）
INSERT INTO account (account_no, company, bank, data_source, currency)
VALUES ('TEST_ACC_001', '【测试企业】华为技术有限公司', '【测试银行】工商银行', '直联', 'CNY'),
       ('TEST_ACC_002', '【测试企业】阿里巴巴集团', '【测试银行】建设银行', '直联', 'CNY'),
       ('TEST_ACC_003', '【测试企业】腾讯科技', '【测试银行】农业银行', '手动上传', 'USD'),
       ('TEST_ACC_004', '【测试企业】字节跳动', '【测试银行】中国银行', '直联', 'EUR'),
       ('TEST_ACC_005', '【测试企业】小米科技', '【测试银行】招商银行', '直联', 'CNY');

-- 3. 插入测试流水数据（重点！用于验证同步！）
-- TEST_ACC_001 作为本方账号的流水
INSERT INTO transaction_flow (our_company, our_account, our_bank, counterpart_account, counterpart_bank,
                              currency, income, expense, balance, summary, trade_type, create_time)
VALUES ('【测试企业】华为技术有限公司', 'TEST_ACC_001', '【测试银行】工商银行', 'OTHER_ACC_001', '其他银行',
        'CNY', 0.00, 1000.00, 999000.00, '测试付款-供应商货款', '3', NOW()),
       ('【测试企业】华为技术有限公司', 'TEST_ACC_001', '【测试银行】工商银行', 'OTHER_ACC_002', '其他银行',
        'CNY', 2000.00, 0.00, 1001000.00, '测试收款-客户回款', '2', NOW()),
       ('【测试企业】华为技术有限公司', 'TEST_ACC_001', '【测试银行】工商银行', 'TEST_ACC_002', '【测试银行】建设银行',
        'CNY', 3000.00, 0.00, 1004000.00, '测试转账-内部调拨', '1', NOW());

-- TEST_ACC_001 作为对方账号的流水
INSERT INTO transaction_flow (our_company, our_account, our_bank, counterpart_account, counterpart_bank,
                              currency, income, expense, balance, summary, trade_type, create_time)
VALUES ('【测试企业】其他公司', 'OTHER_ACC_003', '其他银行', 'TEST_ACC_001', '【测试银行】工商银行',
        'CNY', 4000.00, 0.00, 4000.00, '测试收款-华为付款', '2', NOW()),
       ('【测试企业】腾讯科技', 'TEST_ACC_003', '【测试银行】农业银行', 'TEST_ACC_001', '【测试银行】工商银行',
        'USD', 5000.00, 0.00, 5000.00, '测试收款-华为美金付款', '2', NOW());

-- TEST_ACC_002 作为本方账号的流水
INSERT INTO transaction_flow (our_company, our_account, our_bank, counterpart_account, counterpart_bank,
                              currency, income, expense, balance, summary, trade_type, create_time)
VALUES ('【测试企业】阿里巴巴集团', 'TEST_ACC_002', '【测试银行】建设银行', 'TEST_ACC_001', '【测试银行】工商银行',
        'CNY', 0.00, 6000.00, 994000.00, '测试付款-华为', '3', NOW()),
       ('【测试企业】阿里巴巴集团', 'TEST_ACC_002', '【测试银行】建设银行', 'OTHER_ACC_004', '其他银行',
        'CNY', 0.00, 7000.00, 987000.00, '测试付款-其他供应商', '3', NOW());

-- TEST_ACC_003 作为本方账号的流水
INSERT INTO transaction_flow (our_company, our_account, our_bank, counterpart_account, counterpart_bank,
                              currency, income, expense, balance, summary, trade_type, create_time)
VALUES ('【测试企业】腾讯科技', 'TEST_ACC_003', '【测试银行】农业银行', 'TEST_ACC_004', '【测试银行】中国银行',
        'USD', 0.00, 8000.00, 92000.00, '测试付款-字节', '3', NOW());

-- TEST_ACC_004 作为本方账号的流水
INSERT INTO transaction_flow (our_company, our_account, our_bank, counterpart_account, counterpart_bank,
                              currency, income, expense, balance, summary, trade_type, create_time)
VALUES ('【测试企业】字节跳动', 'TEST_ACC_004', '【测试银行】中国银行', 'TEST_ACC_005', '【测试银行】招商银行',
        'EUR', 0.00, 9000.00, 91000.00, '测试付款-小米', '3', NOW());

-- TEST_ACC_005 作为本方账号的流水
INSERT INTO transaction_flow (our_company, our_account, our_bank, counterpart_account, counterpart_bank,
                              currency, income, expense, balance, summary, trade_type, create_time)
VALUES ('【测试企业】小米科技', 'TEST_ACC_005', '【测试银行】招商银行', 'TEST_ACC_002', '【测试银行】建设银行',
        'CNY', 10000.00, 0.00, 100000.00, '测试收款-阿里', '2', NOW());

-- =============================================
-- 说明：
-- 1. 所有测试数据都有【测试】前缀，一眼就能看出来
-- 2. 重点演示账号：TEST_ACC_001
-- 3. 修改 TEST_ACC_001 的银行、企业、币种、账号后，
--    会自动同步到 transaction_flow 表中作为本方和对方的记录
-- =============================================
