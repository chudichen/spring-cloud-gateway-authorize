package com.michael.gateway.authorize.config;

import com.michael.gateway.authorize.details.dto.PermissionDTO;
import com.michael.gateway.authorize.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理
 *
 * @author Michael.Chu
 * @date 2020-04-02
 */
@Slf4j
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTUtil jwtUtil;

    public AuthenticationManager(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        Claims claims;
        try {
            claims = jwtUtil.getAllClaimsFromToken(authToken);
        } catch (ExpiredJwtException e) {
            return Mono.empty();
        } catch (Exception e) {
            log.error("获取JWT出错,", e);
            return null;
        }
        String username = jwtUtil.getUsernameFromToken(claims);
        if (username != null && jwtUtil.validateToken(authToken) && jwtUtil.validateAccessToken(claims)) {
            List<PermissionDTO> permissions = jwtUtil.getPermissions(claims);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    permissions
            );
            return Mono.just(auth);
        }else{
            return Mono.empty();
        }
    }
}
