package com.ylgj.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：统一异常出口。
 * <p>
 * 各 Controller 无需再手写 try/catch 返回 Result(false)，业务失败直接抛出
 * {@link BizException}，其余异常由本类兜底并记录日志，避免堆栈细节泄露到前端。
 *
 * @author ylgj
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常：直接以异常 message 作为提示返回 */
    @ExceptionHandler(BizException.class)
    public Result handleBizException(BizException e) {
        return new Result(false, e.getMessage());
    }

    /** 参数非法（如 ID/日期解析失败、数值越界等） */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        String message = e.getMessage();
        return new Result(false, message == null || message.isEmpty() ? "参数不合法" : message);
    }

    /** 兜底异常：记录完整日志，向前端返回通用提示，不泄露内部堆栈 */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("[全局异常] 未捕获异常", e);
        return new Result(false, "系统繁忙，请稍后重试");
    }
}
