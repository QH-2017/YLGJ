package com.ylgj.commons;

/**
 * 业务异常：供 Service / Controller 主动抛出，
 * 由 {@link GlobalExceptionHandler} 统一转换为 Result(false, message) 返回，
 * 避免各层重复 try/catch 与 e.printStackTrace()。
 *
 * @author ylgj
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
