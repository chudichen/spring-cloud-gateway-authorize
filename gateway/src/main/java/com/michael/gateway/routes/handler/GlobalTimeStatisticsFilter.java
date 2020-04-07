package com.michael.gateway.routes.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局时间静态统计
 *
 * @author Michael.Chu
 * @date 2020/04/02
 */
@Slf4j
@Component
public class GlobalTimeStatisticsFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            String value = exchange.getRequest().getPath().contextPath().value();
            stopWatch.stop();
            log.info(value + " 本次操作计时：" + stopWatch.getTotalTimeMillis() + "ms");
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
