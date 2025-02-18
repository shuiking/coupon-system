package com.lk.settlement.config;

import com.lk.settlement.common.context.UserContext;
import com.lk.settlement.common.context.UserInfoDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户相关配置类
 *
 * @Author : lk
 * @create 2025/2/19
 */

@Configuration
public class UserConfiguration implements WebMvcConfigurer {
    /**
     * 用户信息传输拦截器
     */
    @Bean
    public UserTransmitInterceptor userTransmitInterceptor() {
        return new UserTransmitInterceptor();
    }


    /**
     * 添加用户信息传递过滤器至相关路径拦截
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTransmitInterceptor())
                .addPathPatterns("/**");
    }

    static class UserTransmitInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) {
            // 这里先通过模拟的形式代替
            UserInfoDTO userInfoDTO = new UserInfoDTO("1810518709471555585", "pdd45305558318", 1810714735922956666L);
            UserContext.setUser(userInfoDTO);
            return true;
        }

        @Override
        public void afterCompletion(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler, Exception exception) {
            UserContext.removeUser();
        }
    }
}
