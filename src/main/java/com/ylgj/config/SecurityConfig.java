package com.ylgj.config;

import com.alibaba.fastjson.JSON;
import com.ylgj.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security 配置
 * <p>
 * 采用 JWT 无状态认证：
 * - 关闭 CSRF、Session
 * - 白名单接口（登录/注册/移动端匿名业务/静态资源）放行
 * - 其余接口需携带有效 Token
 * - 未认证返回 401 JSON，无权限返回 403 JSON
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /** 匿名放行接口（白名单） */
    private static final String[] PERMIT_ALL_URLS = {
            // 认证相关
            "/user/login", "/user/register",
            // AI 健康助手（移动端匿名使用）
            "/ai/**",
            // 移动端匿名业务接口（套餐浏览、预约提交、按手机号查报告）
            "/setmeal/findAll", "/setmeal/findById/**",
            "/order/submit", "/order/findByPhone",
            "/member/findPage",
            // 报表导出下载
            "/report/export**",
            // 静态资源与页面
            "/mobile/**", "/pages/**", "/static/**", "/uploads/**",
            "/favicon.ico", "/error"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离，关闭 CSRF
                .csrf().disable()
                // 无状态会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().and()
                .authorizeRequests(auth -> auth
                        .antMatchers(PERMIT_ALL_URLS).permitAll()
                        .anyRequest().authenticated())
                // 未认证：返回 401
                .exceptionHandling()
                .authenticationEntryPoint((request, response, e) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    Map<String, Object> body = new HashMap<>();
                    body.put("flag", false);
                    body.put("message", "未登录或登录已过期，请重新登录");
                    response.getWriter().write(JSON.toJSONString(body));
                })
                // 无权限：返回 403
                .accessDeniedHandler((request, response, e) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    Map<String, Object> body = new HashMap<>();
                    body.put("flag", false);
                    body.put("message", "无权限访问该资源");
                    response.getWriter().write(JSON.toJSONString(body));
                })
                .and()
                // JWT 过滤器放在用户名密码认证过滤器之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * CORS 配置（与 WebConfig 保持一致）
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
