package com.finlink.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author 稚名不带撇
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    //---------------------------2xx

    /**
     * 操作成功
     */
    SUCCESS(200000, "操作成功"),

    //------------------------4xx
    // 400

    /**
     * 无效的参数
     */
    INVALID_PARA(400000, "无效的参数"),

    /**
     * 账号或密码错误
     */
    ERROR_PHONE_FORMAT(400003, "账号或密码错误"),

    /**
     * 参数类型不匹配
     */
    PARA_TYPE_MISMATCH(400006, "参数类型不匹配"),

    /**
     * 请求过于频繁（频控 / 防刷）
     */
    REQUEST_TOO_FREQUENT(400020, "请求过于频繁，请稍后重试"),

    /**
     * 账号已停用，登录失败
     */
    USER_DISABLE(400007, "账号已停用，登录失败"),

    // 401

    /**
     * 令牌不能为空
     */
    TOKEN_EMPTY(401000, "令牌不能为空"),

    /**
     * 令牌已过期或验证不正确！
     */
    TOKEN_INVALID(401001, "令牌已过期或验证不正确！"),

    /**
     * 令牌已过期！
     */
    TOKEN_OVERTIME(401002, "令牌已过期！"),

    /**
     * 登录状态已过期！
     */
    LOGIN_STATUS_OVERTIME(401003, "登录状态已过期！"),

    /**
     * 令牌验证失败！
     */
    TOKEN_CHECK_FAILED(401004, "令牌验证失败！"),

    //404

    /**
     * 服务未找到！
     */
    SERVICE_NOT_FOUND(404000, "服务未找到"),

    /**
     * url 未找到
     */
    URL_NOT_FOUND(404001, "url 未找到"),

    //405

    /**
     * 请求方法不支持！
     */
    REQUEST_METHOD_NOT_SUPPORTED(405000, "请求方法不支持"),

    //---------------------5xx

    /**
     * 服务繁忙请稍后重试！
     */
    ERROR(500000, "服务繁忙请稍后重试"),

    /**
     * 操作失败
     */
    FAILED(500001, "操作失败"),

    /**
     * Excel 导出失败
     */
    EXCEL_EXPORT_FAILED(500016, "Excel 导出失败"),

    /**
     * Excel 导入失败
     */
    EXCEL_IMPORT_FAILED(500017, "Excel 导入失败"),

    /**
     * Excel 格式错误
     */
    EXCEL_FORMAT_ERROR(500018, "Excel 格式错误"),

    /**
     * Excel 数据验证失败
     */
    EXCEL_VALIDATE_FAILED(500019, "Excel 数据验证失败"),

    /**
     * Excel 文件读取失败
     */
    EXCEL_READ_FAILED(500020, "Excel 文件读取失败"),

    /**
     * Excel 文件写入失败
     */
    EXCEL_WRITE_FAILED(500021, "Excel 文件写入失败"),


    //---------------------枚举占位
    /**
     * 占位专用
     */
    RESERVED(99999999, "占位专用");

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String errMsg;
}