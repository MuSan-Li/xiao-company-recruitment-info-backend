package cn.xiao.company.exception;

import cn.xiao.company.common.BaseResponse;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author xiao
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(HttpServletRequest request, BusinessException e) {
        log.error("BusinessExceptionRequest:{}", request);
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(HttpServletRequest request, RuntimeException e) {
        log.error("RuntimeExceptionRequest:{}", request);
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
