package com.lk.distribution;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.lk.distribution.dao.mapper")
@EnableFeignClients("com.lk.distribution.remote")
public class DistributionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributionApplication.class, args);
    }

}
