package com.lk.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;


/**
 * 全局过滤器，记录请求的日志信息
 *
 * @Author : lk
 * @create 2024/8/8
 */

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求信息
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();

        // 生成唯一的 traceId 用于跟踪请求
        String traceId = UUID.randomUUID().toString();
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        // 将 traceId 放入 MDC 以便在日志中使用
        MDC.put("traceId", traceId);

        LOG.info("请求URI: {}", request.getURI());
        LOG.info("请求类型: {}", method);
        LOG.info("请求头: {}", request.getHeaders());

        // 如果请求方法是 GET，记录请求参数
        if (method == HttpMethod.GET) {
            LOG.info("请求参数: {}", request.getQueryParams());
        }

        // 异步记录响应时间
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            LOG.info("响应时间：{} ms", duration);
        }));
    }

    @Override
    public int getOrder() {
        // 设置过滤器的执行顺序，负数的优先级较高
        return -1;
    }
}
