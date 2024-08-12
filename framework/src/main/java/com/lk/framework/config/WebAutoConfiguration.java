package com.lk.framework.config;

import com.lk.framework.web.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;

/**
 * Web 组件自动装配
 *
 * @Author : lk
 * @create 2024/8/11
 */
public class WebAutoConfiguration {
    /**
     * 构建全局异常拦截器组件 Bean
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
