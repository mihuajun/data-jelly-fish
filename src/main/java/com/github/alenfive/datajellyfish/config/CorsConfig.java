package com.github.alenfive.datajellyfish.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 允许访问的源，使用 * 可以允许所有源
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的 HTTP 方法
                .allowedHeaders("Authorization", "Content-Type") // 允许的请求头
                .allowCredentials(true) // 是否允许发送 Cookie
                .maxAge(3600); // 预检请求的缓存时间，单位秒
    }
}