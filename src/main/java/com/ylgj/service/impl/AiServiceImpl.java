package com.ylgj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ylgj.commons.BizException;
import com.ylgj.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 健康助手实现：基于 DeepSeek Chat API（免费额度），
 * 未配置 Key 或远程调用失败时自动降级为本地关键词规则回复。
 *
 * @author ylgj
 */
@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.api-url:https://api.deepseek.com/chat/completions}")
    private String apiUrl;

    @Value("${ai.model:deepseek-chat}")
    private String model;

    /** 医疗管家系统提示词 */
    private static final String SYSTEM_PROMPT =
        "你是医疗管家系统的AI健康助手，名叫「小医」。你的职责是：\n" +
        "1. 回答用户关于体检项目、套餐选择的疑问\n" +
        "2. 提供基本的健康科普知识\n" +
        "3. 帮助用户了解各项检查的意义\n" +
        "4. 提供预约体检的建议\n" +
        "注意：你不能代替医生做诊断，如有严重症状请建议用户及时就医。回答要简洁友好，使用中文。";

    @Override
    public String chat(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new BizException("请输入您的问题");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // 未配置 API Key：使用本地智能回复（离线可用）
            return localReply(userMessage);
        }
        try {
            return callDeepSeek(userMessage);
        } catch (Exception e) {
            // 远程调用失败：记录日志并降级为本地回复，保证助手可用
            log.warn("[AI助手] DeepSeek 调用失败，降级为本地回复：{}", e.getMessage());
            return "AI服务暂时不可用，以下为本地助手回复：\n\n" + localReply(userMessage);
        }
    }

    /**
     * 调用 DeepSeek Chat Completions 接口。
     */
    private String callDeepSeek(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", buildMessages(userMessage));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1024);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl, HttpMethod.POST, entity, String.class);

        // 解析响应
        JSONObject responseJson = JSON.parseObject(response.getBody());
        JSONArray choices = responseJson.getJSONArray("choices");
        if (choices != null && choices.size() > 0) {
            String content = choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
            if (content != null) {
                return content.trim();
            }
        }
        throw new BizException("AI回复解析失败");
    }

    /**
     * 构建对话消息列表（系统提示词 + 用户问题）。
     */
    private List<Map<String, String>> buildMessages(String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", SYSTEM_PROMPT);
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return messages;
    }

    @Override
    public String localReply(String message) {
        String msg = message.toLowerCase();

        if (msg.contains("体检") && (msg.contains("推荐") || msg.contains("建议") || msg.contains("选"))) {
            return "🏥 体检套餐推荐：\n\n" +
                "1️⃣ **入职体检套餐**（¥300）：适合入职体检，包含血常规、肝功能、心电图等基础项目\n\n" +
                "2️⃣ **阳光爸妈升级肿瘤筛查**（¥1400）：适合55-100岁中老年人，包含12项肿瘤筛查、肝肾检查、颈动脉彩超等\n\n" +
                "3️⃣ **粉红珍爱女性套餐**（¥1200）：专为女性设计，包含TCT、HPV筛查、乳腺彩超、甲状腺检查等\n\n" +
                "您可以点击左侧菜单的「套餐信息管理」查看详情。";
        }

        if (msg.contains("血常规") || msg.contains("血检")) {
            return "🔬 **血常规检查**是体检中最基础也最重要的检查之一：\n\n" +
                "• **白细胞**：反映身体是否有感染或炎症\n" +
                "• **红细胞**：反映是否有贫血\n" +
                "• **血红蛋白**：判断贫血程度\n" +
                "• **血小板**：反映凝血功能\n\n" +
                "建议：检查前一天避免剧烈运动，空腹抽血效果更佳。";
        }

        if (msg.contains("预约") || msg.contains("挂号")) {
            return "📅 **预约体检指南**：\n\n" +
                "1. 先选择适合您的体检套餐\n" +
                "2. 在「预约条件设置」中查看可预约的日期\n" +
                "3. 选择合适的日期进行预约\n\n" +
                "💡 温馨提示：\n" +
                "• 体检前一天请保持清淡饮食\n" +
                "• 体检当天需要空腹（至少禁食8小时）\n" +
                "• 请携带有效身份证件";
        }

        if (msg.contains("肿瘤") || msg.contains("癌")) {
            return "🎗️ **肿瘤筛查**相关知识：\n\n" +
                "常见肿瘤筛查项目包括：\n" +
                "• **AFP（甲胎蛋白）**：肝癌筛查\n" +
                "• **CEA（癌胚抗原）**：消化系统肿瘤筛查\n" +
                "• **CA125**：卵巢癌筛查\n" +
                "• **胸部CT**：肺癌筛查\n\n" +
                "建议40岁以上人群每年做一次肿瘤标志物检查。如有家族病史，建议提前并增加检查频率。";
        }

        if (msg.contains("你好") || msg.contains("您好") || msg.contains("hi") || msg.contains("hello")) {
            return "👋 您好！我是医疗管家AI助手「小医」。\n\n" +
                "我可以帮您：\n" +
                "🏥 推荐适合的体检套餐\n" +
                "📋 解释各项检查的意义\n" +
                "📅 提供预约体检建议\n" +
                "💡 分享健康科普知识\n\n" +
                "请问有什么可以帮您的？";
        }

        // 默认回复
        return "感谢您的提问！关于「" + message + "」，我建议您：\n\n" +
            "1. 可以查看「套餐信息管理」了解我们提供的体检套餐\n" +
            "2. 如有具体健康问题，建议咨询专业医生\n" +
            "3. 定期体检是预防疾病的最佳方式\n\n" +
            "您可以尝试问我：\n" +
            "• 推荐适合的体检套餐\n" +
            "• 血常规检查有什么意义\n" +
            "• 如何预约体检\n" +
            "• 肿瘤筛查有哪些项目";
    }
}
