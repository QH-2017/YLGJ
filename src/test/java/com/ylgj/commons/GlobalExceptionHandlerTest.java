package com.ylgj.commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

/**
 * {@link GlobalExceptionHandler} 异常出口单元测试。
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void bizException_mapsToFailureResult() {
        Result result = handler.handleBizException(new BizException("订单不存在"));
        assertFalse(result.isFlag());
        assertEquals("订单不存在", result.getMessage());
    }

    @Test
    void illegalArgument_mapsToFailureResult() {
        Result result = handler.handleIllegalArgumentException(new IllegalArgumentException("参数不合法"));
        assertFalse(result.isFlag());
        assertEquals("参数不合法", result.getMessage());
    }

    @Test
    void genericException_hidesInternalDetail() {
        Result result = handler.handleException(new IllegalStateException("内部实现细节"));
        assertFalse(result.isFlag());
        // 不向前端泄露内部堆栈/细节
        assertEquals("系统繁忙，请稍后重试", result.getMessage());
    }
}
