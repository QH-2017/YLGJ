package com.ylgj.service.impl;

import com.ylgj.commons.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AiServiceImpl} 本地规则回复与降级逻辑单元测试（不触网）。
 */
class AiServiceImplTest {

    private final AiServiceImpl aiService = new AiServiceImpl();

    @BeforeEach
    void setUp() {
        // 未配置 API Key → chat 走本地回复分支
        ReflectionTestUtils.setField(aiService, "apiKey", "");
    }

    @Test
    void chat_blankMessage_throwsBiz() {
        assertThrows(BizException.class, () -> aiService.chat(null));
        assertThrows(BizException.class, () -> aiService.chat("   "));
    }

    @Test
    void chat_withoutKey_usesLocalReply() {
        String reply = aiService.chat("你好");
        assertNotNull(reply);
        assertTrue(reply.contains("小医"), "未配置 Key 时应返回本地助手回复");
    }

    @Test
    void localReply_recommendSetmeal() {
        String reply = aiService.localReply("给我推荐一个体检套餐");
        assertTrue(reply.contains("入职体检套餐"));
        assertTrue(reply.contains("阳光爸妈"));
    }

    @Test
    void localReply_bloodTest() {
        assertTrue(aiService.localReply("血常规是查什么的").contains("白细胞"));
    }

    @Test
    void localReply_booking() {
        assertTrue(aiService.localReply("怎么预约体检").contains("预约体检指南"));
    }

    @Test
    void localReply_tumor() {
        assertTrue(aiService.localReply("肿瘤筛查有哪些").contains("肿瘤筛查"));
    }

    @Test
    void localReply_greeting() {
        assertTrue(aiService.localReply("hello").contains("小医"));
    }

    @Test
    void localReply_default_fallback() {
        String reply = aiService.localReply("随便说点不相干的");
        assertTrue(reply.contains("感谢您的提问"));
    }
}
