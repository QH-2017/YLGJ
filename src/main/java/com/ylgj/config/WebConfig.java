package com.ylgj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path:${user.home}/ylgj-uploads/}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(org.springframework.http.CacheControl.noCache().mustRevalidate());
        // 允许直接通过 /pages/ 访问页面
        registry.addResourceHandler("/pages/**")
                .addResourceLocations("classpath:/static/pages/")
                .setCacheControl(org.springframework.http.CacheControl.noCache().mustRevalidate());
        // 移动端页面静态资源映射
        registry.addResourceHandler("/mobile/**")
                .addResourceLocations("classpath:/static/mobile/")
                .setCacheControl(org.springframework.http.CacheControl.noCache().mustRevalidate());
        // 上传文件静态资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCacheControl(org.springframework.http.CacheControl.noCache().mustRevalidate());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有来源的跨域请求
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
