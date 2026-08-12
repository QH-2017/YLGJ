package com.ylgj.service.impl;

import com.ylgj.pojo.User;
import com.ylgj.mapper.UserMapper;
import com.ylgj.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixin
 * @since 2026-07-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /** BCrypt 加密器（加盐哈希，安全性远高于明文/MD5） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String storedPassword) {
        if (isBcrypt(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        // 兼容历史明文密码（平滑迁移：校验通过后由调用方升级为 BCrypt）
        return storedPassword != null && storedPassword.equals(rawPassword);
    }

    @Override
    public boolean isBcrypt(String storedPassword) {
        return storedPassword != null &&
                (storedPassword.startsWith("$2a$")
                        || storedPassword.startsWith("$2b$")
                        || storedPassword.startsWith("$2y$"));
    }
}
