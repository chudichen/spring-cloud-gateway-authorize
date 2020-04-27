package com.michael.gateway.authorize.controller;

import com.michael.gateway.authorize.controller.model.AuthRequest;
import com.michael.gateway.authorize.controller.model.AuthResponse;
import com.michael.gateway.authorize.details.dto.PermissionDTO;
import com.michael.gateway.authorize.details.dto.UserPermissionDTO;
import com.michael.gateway.authorize.details.service.ReactAdminDetailsServiceImpl;
import com.michael.gateway.authorize.utils.JWTUtil;
import com.michael.gateway.authorize.utils.TokenType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限接口
 *
 * @author Michael.Chu
 * @date 2020/03/31
 */
@Slf4j
@RestController
public class AuthenticationController {

    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ReactAdminDetailsServiceImpl userRepository;
    private final RedisTemplate<String, UserPermissionDTO> permissionDomainRedisTemplate;
    // 设置超时时间默认288000
    @Value("${oauth.jwt.expiration.refresh:288000}")
    private Long expirationRefreshTime;

    public AuthenticationController(JWTUtil jwtUtil,
                                    PasswordEncoder passwordEncoder,
                                    ReactAdminDetailsServiceImpl userRepository,
                                    @Qualifier("permissionRedisTemplate") RedisTemplate<String, UserPermissionDTO> permissionDomainRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.permissionDomainRedisTemplate = permissionDomainRedisTemplate;
    }

    @PostMapping(value = "/authorize/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar) {
        return userRepository.findByUsername(ar.getUsername()).map(adminDetails -> {
            if (passwordEncoder.matches(ar.getPassword(),adminDetails.getPassword())) {
                return ResponseEntity.ok(createAuthResponse(adminDetails));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * 根据刷新token生成AccessToken信息
     * @param refreshToken 刷新令牌
     * @return 获取新的刷新令牌的相应
     */
    @PostMapping(value = "/authorize/refreshToken")
    public Mono<ResponseEntity<?>> refreshToken(@RequestBody @NonNull String refreshToken) throws IOException {
        String accessToken = jwtUtil.createAccessTokenByRefreshToken(refreshToken);
        if(accessToken == null){
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        String username = jwtUtil.getUsernameFromToken(jwtUtil.getAllClaimsFromToken(refreshToken));
        return Mono.just(ResponseEntity.ok(new AuthResponse(username,accessToken,refreshToken)));
    }

    private AuthResponse createAuthResponse(UserDetails userDetails){
        String tokenId = UUID.randomUUID().toString();
        String accessToken = jwtUtil.generateToken(userDetails, TokenType.ACCESS, tokenId);
        String refreshToken = jwtUtil.generateToken(userDetails, TokenType.REFRESH, tokenId);
        //校验成功后，将权限信息保存至redis中
        UserPermissionDTO adminDetails = (UserPermissionDTO) userDetails;
        savePermissionToRedis(adminDetails,tokenId);

        return new AuthResponse(userDetails.getUsername(),accessToken,refreshToken);
    }

    private void savePermissionToRedis(UserPermissionDTO userPermissionDTO,String tokenId){
        String redisKey = "perm_"+ userPermissionDTO.getUsername() + "_" + tokenId;
        permissionDomainRedisTemplate.opsForList().leftPushAll(redisKey, userPermissionDTO);
        permissionDomainRedisTemplate.expire(redisKey,expirationRefreshTime, TimeUnit.SECONDS);
    }
}

