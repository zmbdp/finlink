package com.finlink.common.constants;

/**
 * 通用常量
 *
 * @author 稚名不带撇
 */
public class CommonConstants {

    /**
     * 标准时间格式
     */
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认编码
     */
    public final static String UTF8 = "UTF-8";

    /**
     * 默认分隔符
     */
    public final static String DEFAULT_DELIMITER = "; ";

    /**
     * 逗号分隔符
     */
    public static final String COMMA_SEPARATOR = ",";

    /**
     * 井号分隔符（#）
     * <p>
     * 用于拼接类名和方法名，格式：类名#方法名
     */
    public static final String HASH_SEPARATOR = "#";

    /**
     * 点号分隔符（.）
     * <p>
     * 用于拼接类名和方法名，格式：类名.方法名
     */
    public static final String DOT_SEPARATOR = ".";

    /**
     * 冒号分隔符（:）
     * <p>
     * 常用于 Redis Key 的拼接，格式：前缀:业务:ID
     */
    public static final String COLON_SEPARATOR = ":";

    /**
     * 空字符串
     */
    public final static String EMPTY_STR = "";

    /*=============================================    通用状态常量    =============================================*/

    /**
     * 状态：成功
     */
    public static final String STATUS_SUCCESS = "SUCCESS";

    /**
     * 状态：失败
     */
    public static final String STATUS_FAILED = "FAILED";

    /**
     * 状态：处理中
     */
    public static final String STATUS_PROCESSING = "PROCESSING";

    /**
     * 未知标识
     * <p>
     * 某些代理服务器在无法获取真实信息时会设置此值
     */
    public static final String UNKNOWN = "unknown";

    /*=============================================    线程池常量    =============================================*/

    /**
     * 异步线程池名字
     */
    public final static String ASYNCHRONOUS_THREADS_BEAN_NAME = "threadPoolTaskExecutor";

    /**
     * 定时任务线程池名字
     */
    public final static String SCHEDULED_THREADS_BEAN_NAME = "scheduledExecutorService";

    /*=============================================    分页常量    =============================================*/

    /**
     * 默认当前页码
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页条数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /*=============================================    日期格式常量    =============================================*/

    /**
     * 日期格式：yyyyMMdd
     */
    public static final String DATE_FORMAT_SHORT = "yyyyMMdd";

    /*=============================================    文件导出常量    =============================================*/

    /**
     * Excel 文件名前缀 - 账号列表
     */
    public static final String EXCEL_FILE_NAME_ACCOUNT = "账号列表_";

    /**
     * Excel 文件名前缀 - 流水列表
     */
    public static final String EXCEL_FILE_NAME_FLOW = "流水列表_";

    /**
     * Excel 文件后缀
     */
    public static final String EXCEL_FILE_SUFFIX = ".xlsx";

    /**
     * Excel Sheet 名称 - 账号列表
     */
    public static final String EXCEL_SHEET_NAME_ACCOUNT = "账号列表";

    /**
     * Excel Sheet 名称 - 流水列表
     */
    public static final String EXCEL_SHEET_NAME_FLOW = "流水列表";

    /**
     * Excel Content-Type
     */
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /*=============================================    JWT 常量    =============================================*/

    /**
     * JWT Token 有效期：24小时（毫秒）
     */
    public static final long JWT_EXPIRE_SHORT = 24 * 60 * 60 * 1000L;

    /**
     * JWT Token 有效期：7天（毫秒）
     */
    public static final long JWT_EXPIRE_REMEMBER = 7 * 24 * 60 * 60 * 1000L;

    /*=============================================    业务错误提示常量    =============================================*/

    /**
     * 账号不存在
     */
    public static final String ERROR_MSG_ACCOUNT_NOT_FOUND = "账号不存在";

    /**
     * 账号已存在
     */
    public static final String ERROR_MSG_ACCOUNT_EXISTS = "账号已存在";

    /**
     * 账号已关联流水记录，无法删除
     */
    public static final String ERROR_MSG_ACCOUNT_HAS_FLOWS = "该账号已关联 %d 条流水记录，无法删除";

    /**
     * Excel 导出失败
     */
    public static final String ERROR_MSG_EXCEL_EXPORT_FAILED = "Excel 导出失败";
}