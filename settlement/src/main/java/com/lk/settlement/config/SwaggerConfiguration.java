package com.lk.settlement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 设置文档 API Swagger 配置信息
 *
 * @Author : lk
 * @create 2024/8/25
 */

@Slf4j
@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements ApplicationRunner {

    @Value("${server.port:8080}")
    private String serverPort;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Value("${knife4j.enable}")
    private boolean enable;


    /**
     * 自定义 openAPI 个性化信息
     *
     * @return
     */

    @Bean
    public Docket Api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lk.engine.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 添加摘要信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("结算系统")
                .description("负责用户下单时订单金额计算功能，因和订单相关联，该服务流量较大")
                .version("v1.0.0")
                .build();
    }


    /**
     * 启动项目后接口文档的展示
     *
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("API Document: http://127.0.0.1:{}{}/doc.html", serverPort, contextPath);
    }
}
