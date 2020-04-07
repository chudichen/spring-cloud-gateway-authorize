package com.michael.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 启动类
 *
 * @author Michael.Chu
 * @date 2020-04-02
 */
@EnableDiscoveryClient
@SpringBootApplication
public class WebfluxGatewayAuthorizeApplication {
    /**
     * 未完成事项
     * 1.设计刷新token
     * 2.设计权限保存进redis
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(WebfluxGatewayAuthorizeApplication.class, args);
    }
}
