package com.ylgj.service.impl;

import com.ylgj.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link UserServiceImpl} 密码安全逻辑单元测试（纯内存，无需数据库）。
 */
class UserServiceImplTest {

    private final UserServiceImpl userService = new UserServiceImpl();

    @Test
    void encodePassword_producesBcryptHash() {
        String hash = userService.encodePassword("123456");
        assertNotNull(hash);
        // BCrypt 哈希以 $2a/$2b/$2y 开头
        assertTrue(userService.isBcrypt(hash), "加密结果应被识别为 BCrypt");
    }

    @Test
    void encodePassword_sameInputDifferentSalt() {
        // BCrypt 内置随机盐：同一明文两次加密结果不同，抗彩虹表
        assertNotEquals(userService.encodePassword("123456"),
                userService.encodePassword("123456"));
    }

    @Test
    void matchesPassword_bcryptRoundTrip() {
        String hash = userService.encodePassword("abc123");
        assertTrue(userService.matchesPassword("abc123", hash));
        assertFalse(userService.matchesPassword("wrong", hash));
    }

    @Test
    void matchesPassword_legacyPlaintextCompatibility() {
        // 历史明文密码（BCrypt 平滑迁移前）校验仍需通过
        assertTrue(userService.matchesPassword("123456", "123456"));
        assertFalse(userService.matchesPassword("123456", "654321"));
        assertFalse(userService.matchesPassword("123456", null));
    }

    @Test
    void isBcrypt_detectsLegacyPlaintext() {
        assertFalse(userService.isBcrypt("123456"));
        assertFalse(userService.isBcrypt(null));
        assertTrue(userService.isBcrypt("$2a$10$abcdefghijklmnopqrstuu"));
    }
}
