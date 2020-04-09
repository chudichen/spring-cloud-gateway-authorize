package com.michael.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Michael Chu
 * @since 2020-04-07 10:52
 */
@RestController
@RequestMapping("/api/")
public class AdminController {

    private static final String USER_ID = "user_id";

    @GetMapping("test")
    public Mono<String> test01(@RequestHeader(USER_ID)String userId) {
        return Mono.just("test " + userId);
    }
}
