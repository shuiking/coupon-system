package com.lk.distribution.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置类
 * @Author : lk
 * @create 2024/8/30
 */

@Configuration
@MapperScan("com.nageoffer.onecoupon.settlement.dao.mapper")
public class MyBatisConfig {
}
