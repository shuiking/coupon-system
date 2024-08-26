package com.lk.merchantadmin;

import com.mzt.logapi.starter.annotation.EnableLogRecord;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableLogRecord(tenant = "MerchantAdmin")
@MapperScan("com.lk.merchantadmin.dao.mapper")
public class MerchantAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantAdminApplication.class, args);
    }

}
