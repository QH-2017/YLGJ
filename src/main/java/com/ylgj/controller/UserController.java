package com.ylgj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylgj.commons.Result;
import com.ylgj.pojo.User;
import com.ylgj.security.JwtUtil;
import com.ylgj.security.SecurityUser;
import com.ylgj.service.UserService;
import com.ylgj.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * <p>
 * 认证流程（JWT + Redis）：
 * 1. 登录：BCrypt 校验密码（兼容历史明文自动升级）→ 生成 JWT → 存入 Redis → 返回 Token
 * 2. 登录失败次数限制：连续失败 5 次锁定账号 10 分钟（Redis 计数）
 * 3. 登出/改密：删除 Redis 中的 Token，实现立即失效
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /** 登录失败锁定阈值 */
    private static final int MAX_FAIL_COUNT = 5;
    /** 锁定时间（秒）：10 分钟 */
    private static final long LOCK_SECONDS = 600;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户登录（返回 JWT Token）
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()
                || user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return new Result(false, "用户名和密码不能为空");
        }
        String username = user.getUsername().trim();

        // 账号锁定检查
        String failKey = RedisUtil.LOGIN_FAIL_PREFIX + username;
        String failCount = redisUtil.get(failKey);
        if (failCount != null && Integer.parseInt(failCount) >= MAX_FAIL_COUNT) {
            return new Result(false, "登录失败次数过多，账号已锁定，请10分钟后再试");
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User loginUser = userService.getOne(wrapper);
        if (loginUser == null || !userService.matchesPassword(user.getPassword(), loginUser.getPassword())) {
            recordLoginFail(failKey);
            return new Result(false, "用户名或密码错误");
        }

        // 历史明文密码自动升级为 BCrypt（平滑迁移）
        if (!userService.isBcrypt(loginUser.getPassword())) {
            loginUser.setPassword(userService.encodePassword(user.getPassword()));
            userService.updateById(loginUser);
        }

        // 登录成功：清除失败计数，签发 Token 并存入 Redis
        redisUtil.delete(failKey);
        String token = jwtUtil.generateToken(username, loginUser.getId());
        redisUtil.set(RedisUtil.LOGIN_TOKEN_PREFIX + token, username, jwtUtil.getExpire());
        redisUtil.set(RedisUtil.LOGIN_USER_PREFIX + username, token, jwtUtil.getExpire());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", username);
        data.put("userId", loginUser.getId());
        return new Result(true, "登录成功", data);
    }

    /**
     * 退出登录（删除 Redis 中的 Token，立即失效）
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = redisUtil.get(RedisUtil.LOGIN_TOKEN_PREFIX + token);
            if (username != null) {
                redisUtil.delete(RedisUtil.LOGIN_TOKEN_PREFIX + token);
                redisUtil.delete(RedisUtil.LOGIN_USER_PREFIX + username);
            }
        }
        return new Result(true, "退出登录成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            User user = userService.getById(securityUser.getUserId());
            Map<String, Object> data = new HashMap<>();
            data.put("username", securityUser.getUsername());
            data.put("userId", securityUser.getUserId());
            if (user != null) {
                data.put("telephone", user.getTelephone());
                data.put("gender", user.getGender());
            }
            return new Result(true, "获取用户信息成功", data);
        }
        return new Result(false, "未登录");
    }

    @GetMapping("/findAll")
    public Result findAll() {
        return new Result(true, "查询用户成功", userService.list());
    }

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return new Result(false, "密码长度不能少于6位");
        }
        try {
            user.setPassword(userService.encodePassword(user.getPassword()));
            userService.save(user);
            return new Result(true, "新增用户成功");
        } catch (Exception e) {
            return new Result(false, "新增用户失败");
        }
    }

    /**
     * 校验用户名是否已被占用（注册页公开调用，无需登录）。
     *
     * @return data 为 "available"（可用）或 "taken"（已被占用）
     */
    @GetMapping("/checkUsername")
    public Result checkUsername(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return new Result(false, "用户名不能为空");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username.trim());
        boolean exists = userService.count(wrapper) > 0;
        return new Result(true, exists ? "用户名已被注册" : "用户名可用",
                exists ? "taken" : "available");
    }

    /**
     * 用户注册（密码 BCrypt 加密存储）
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return new Result(false, "用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return new Result(false, "密码不能为空");
        }
        if (user.getPassword().length() < 6) {
            return new Result(false, "密码长度不能少于6位");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername().trim());
        long count = userService.count(wrapper);
        if (count > 0) {
            return new Result(false, "用户名已存在，请更换");
        }
        try {
            user.setStation("1");
            user.setPassword(userService.encodePassword(user.getPassword()));
            userService.save(user);
            return new Result(true, "注册成功，请登录");
        } catch (Exception e) {
            return new Result(false, "注册失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息（密码留空则不修改原密码）
     */
    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        if (user.getId() == null) {
            return new Result(false, "用户ID不能为空");
        }
        // 密码留空则不更新密码（updateById 忽略 null 字段，避免覆盖原密码）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(null);
        } else {
            user.setPassword(userService.encodePassword(user.getPassword()));
        }
        try {
            userService.updateById(user);
            return new Result(true, "更新用户成功");
        } catch (Exception e) {
            return new Result(false, "更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            userService.removeById(id);
            return new Result(true, "删除用户成功");
        } catch (Exception e) {
            return new Result(false, "删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码（校验原密码，新密码 BCrypt 加密，成功后强制下线）
     */
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (username == null || oldPassword == null || newPassword == null) {
            return new Result(false, "参数不完整");
        }
        if (newPassword.length() < 6) {
            return new Result(false, "新密码长度不能少于6位");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return new Result(false, "用户不存在");
        }
        if (!userService.matchesPassword(oldPassword, user.getPassword())) {
            return new Result(false, "原密码错误");
        }
        user.setPassword(userService.encodePassword(newPassword));
        userService.updateById(user);

        // 修改密码后强制下线：删除 Redis 中该用户的 Token
        String token = redisUtil.get(RedisUtil.LOGIN_USER_PREFIX + username);
        if (token != null) {
            redisUtil.delete(RedisUtil.LOGIN_TOKEN_PREFIX + token);
            redisUtil.delete(RedisUtil.LOGIN_USER_PREFIX + username);
        }
        return new Result(true, "密码修改成功，请重新登录");
    }

    /**
     * 记录登录失败次数（首次失败时设置锁定过期时间）
     */
    private void recordLoginFail(String failKey) {
        long count = redisUtil.increment(failKey);
        if (count == 1) {
            redisUtil.expire(failKey, LOCK_SECONDS);
        }
    }
}
