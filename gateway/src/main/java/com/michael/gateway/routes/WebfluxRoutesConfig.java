package com.michael.gateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * webFlux路由配置
 *
 * @author Michael.Chu
 * @date 2020-04-02
 */
@Slf4j
@Configuration
public class WebfluxRoutesConfig {

    private static final String CLIENT_PATH = "/api/client";
    private static final String ADMIN_PATH = "/api/admin";
    private static final String API_PREFIX = "/api";


    /**
     * 用户登陆，此时请求授权系统申请令牌。
     * 添加拦截器进行区分，登陆操作时为其赋值clientId以及ClientSecret，并返回令牌信息
     * 登出操作时获取token令牌，并进行注销
     *
     * @param builder
     * @return
     */
    @Bean(name = "client-route")
    public RouteLocator authorizationLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path(CLIENT_PATH + "/**")
                        .filters(f -> {
                            f.stripPrefix(2);
                            f.prefixPath(API_PREFIX);
                            return f;
                        })
                        .uri("lb://SPRING-CLOUD-GATEWAY-SERVICE")
                        .order(2)
                        .id("client-route")
                ).build();
    }

    @Bean("admin-route")
    public RouteLocator adminLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path(ADMIN_PATH + "/**")
                        .filters( f -> {
                            f.stripPrefix(2);
                            f.prefixPath(API_PREFIX);
                            return f;
                        })
                        .uri("lb://SPRING-CLOUD-GATEWAY-SERVICE")
                        .order(2)
                        .id("admin-route")
                ).build();
    }

}
