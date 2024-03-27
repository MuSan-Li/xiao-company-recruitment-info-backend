package cn.xiao.company.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义错误码
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(0, "ok"),

    PARAMS_ERROR(40000, "请求参数错误"),

    NOT_LOGIN_ERROR(40100, "未登录"),

    NO_AUTH_ERROR(40101, "无权限"),

    NOT_FOUND_ERROR(40400, "请求数据不存在"),

    TOO_MANY_REQUEST(42900, "请求过于频繁，请稍后再试！"),

    FORBIDDEN_ERROR(40300, "禁止访问"),

    SYSTEM_ERROR(50000, "系统内部异常"),

    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

}
