package com.michael.gateway.authorize.config;

import com.michael.gateway.authorize.details.domain.status.RequestMethodType;
import com.michael.gateway.authorize.details.dto.PermissionDTO;
import com.michael.gateway.authorize.details.dto.UserPermissionDTO;
import com.michael.gateway.authorize.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * 安全上下文
 *
 * @author Michael.Chu
 * @date 2020-04-02
 */
@Slf4j
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private static final String AUTHORIZE_PREFIX = "authorize ";
    private static final String USER_ID = "user_id";

    /**
     * 短路配置
     */
    @Value("${authorize.permission.enable:true}")
    private boolean enablePermission;
    @Value("${authorize.enable:true}")
    private boolean enableAuthorize;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public SecurityContextRepository(AuthenticationManager authenticationManager,
                                     JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(AUTHORIZE_PREFIX)) {
            String jwtToken = authHeader.substring(AUTHORIZE_PREFIX.length());
            Authentication auth = new UsernamePasswordAuthenticationToken(jwtToken, jwtToken);
            Mono<SecurityContext> result = this.authenticationManager.authenticate(auth)
                    .filter(authentication -> (!enablePermission) || validatePermission(jwtToken, exchange.getRequest()))
                    .map(SecurityContextImpl::new);

            exchange.mutate().request(
                    exchange.getRequest().mutate()
                            .headers(httpHeaders -> replaceHeader(httpHeaders, jwtToken))
                            .build()
            ).build();
            return result;
        } else {
            return Mono.empty();
        }
    }

    /**
     * 替换headers，删除Authorization，添加userId
     *
     * @param httpHeaders header
     * @param token jwt
     */
    private void replaceHeader(HttpHeaders httpHeaders, String token) {
        httpHeaders.remove(HttpHeaders.AUTHORIZATION);
        Long userId = jwtUtil.getUserId(token);
        httpHeaders.set(USER_ID, userId.toString());
    }

    /**
     * 是否满足权限判断
     *
     * @param authToken token
     * @param request 请求
     * @return {@code true}表示满足
     */
    private boolean validatePermission(String authToken, ServerHttpRequest request){
        if (request.getMethod() == null) {
            return true;
        }

        // 判断permission权限
        Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
        List<PermissionDTO> permissions = jwtUtil.getPermissions(claims);

        if(!CollectionUtils.isEmpty(permissions)){
            return validateUri(permissions, request);
        }
        return true;
    }

    /**
     * 判断是否可以发起请求
     *
     * @param permissionDTOs 权限
     * @param request 请求地址
     * @return {@code true}表示满足
     */
    private boolean validateUri(List<PermissionDTO> permissionDTOs, ServerHttpRequest request) {
        if (Objects.isNull(request.getMethod())) {
            return false;
        }

        String requestPath = (request.getPath().value().startsWith("/api"))?request.getPath().value().substring(4):request.getPath().value();
        Optional<PermissionDTO> hasPermission = permissionDTOs.stream()
                .filter(permission -> antPathMatcher.match(permission.getUri(), requestPath))
                .filter(permission -> (permission.getRequestType() == RequestMethodType.ALL
                        || StringUtils.equalsIgnoreCase(request.getMethod().name(), permission.getRequestType().name())))
                .findAny();

        log.info("请求路径：" + request.getPath().value() +",请求方式：" + request.getMethod().name() + ",判断结果：" + hasPermission.isPresent());
        return hasPermission.isPresent();
    }
}
