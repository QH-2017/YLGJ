package com.ylgj.service;

/**
 * AI 健康助手服务：把第三方大模型调用与本地兜底规则从 Controller 下沉到 Service，
 * 便于复用、降级策略统一与单元测试。
 *
 * @author ylgj
 */
public interface AiService {

    /**
     * 与 AI 助手对话，返回助手回复文本。
     * <ul>
     *   <li>消息为空 → 抛 {@link com.ylgj.commons.BizException}</li>
     *   <li>未配置 API Key → 返回本地规则回复</li>
     *   <li>已配置 API Key → 调用 DeepSeek，远程异常时自动降级为本地回复</li>
     * </ul>
     */
    String chat(String userMessage);

    /**
     * 本地关键词规则回复（无需外部 API，便于离线演示与单元测试）。
     */
    String localReply(String message);
}
