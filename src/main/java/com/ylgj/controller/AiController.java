package com.ylgj.controller;

import com.ylgj.commons.Result;
import com.ylgj.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AI 智能助手控制器（瘦身版）。
 * <p>只做参数提取与响应组装，实际对话逻辑（DeepSeek 调用 / 本地兜底 / 降级）
 * 统一收敛在 {@link AiService}，便于复用与单元测试。
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 发送消息给 AI 并获取回复
     */
    @PostMapping("/chat")
    public Result chat(@RequestBody Map<String, Object> request) {
        String userMessage = (String) request.get("message");
        String reply = aiService.chat(userMessage);
        return new Result(true, "回复成功", reply);
    }
}
