package com.finlink.simple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 简单的测试类，用于验证测试框架是否正常工作。
 * <p>
 * 本类不依赖数据库，仅对基础运算和字符串操作进行断言验证。
 * </p>
 *
 * @author 稚名不带撇
 */
@DisplayName("简单测试")
class SimpleTest {

    /**
     * 测试基础加法运算。
     * <p>
     * 期望：1 + 1 的结果等于 2。
     * </p>
     */
    @Test
    @DisplayName("测试加法")
    void testAddition() {
        int result = 1 + 1;
        assertEquals(2, result, "1 + 1 应等于 2");
    }

    /**
     * 测试字符串相等性。
     * <p>
     * 期望：两个内容相同的字符串应判定为相等。
     * </p>
     */
    @Test
    @DisplayName("测试字符串相等")
    void testStringEquals() {
        String str1 = "Hello";
        String str2 = "Hello";
        assertEquals(str1, str2, "内容相同的字符串应相等");
    }
}