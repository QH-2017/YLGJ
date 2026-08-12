package com.ylgj.service;

import com.ylgj.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lixin
 * @since 2026-07-04
 */
public interface UserService extends IService<User> {

    /**
     * 密码加密（BCrypt）
     */
    String encodePassword(String rawPassword);

    /**
     * 密码校验（兼容历史明文密码，校验通过后由调用方升级为 BCrypt）
     */
    boolean matchesPassword(String rawPassword, String storedPassword);

    /**
     * 判断存储密码是否为 BCrypt 加密格式
     */
    boolean isBcrypt(String storedPassword);
}
