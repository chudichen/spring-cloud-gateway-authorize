package com.michael.gateway.authorize.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Chu
 * @since 2020-04-02 14:50
 */
@Configuration
@MapperScan(basePackages = "com.michael.gateway.authorize.details.mapper")
public class MybatisPlusConfig {
}
