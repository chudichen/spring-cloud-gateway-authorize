package com.michael.gateway.authorize.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.michael.gateway.authorize.details.dto.PermissionDTO;
import com.michael.gateway.authorize.details.dto.UserPermissionDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT工具类
 *
 * @author Michael.Chu
 * @date 2020/03/31
 */
@Slf4j
@Component
public class JWTUtil {

    private static final String TOKEN_ID = "token_id";
    private static final String ROLE_ID = "role";
    private static final String USER_ID = "user_id";

    @Value("${oauth.jwt.secret:secret}")
    private String secret;

    @Value("${oauth.jwt.expiration.access:28800}")
    private String expirationTime;

    @Value("${oauth.jwt.expiration.refresh:288000}")
    private String expirationRefreshTime;

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes())).parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(Claims claims) {
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(Claims claims) {
        return claims.getExpiration();
    }

    private Boolean isTokenExpired(Claims claims) {
        final Date expiration = getExpirationDateFromToken(claims);
        return expiration.before(new Date());
    }

    /**
     * 配置JWT生成参数
     *
     * @param userDetails 用户信息
     * @param tokenType 生成类型
     * @param tokenId jwt的ID
     * @return
     */
    public String generateToken(UserDetails userDetails,TokenType tokenType,String tokenId) {
        UserPermissionDTO adminDetails = (UserPermissionDTO)userDetails;
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(TOKEN_ID, tokenId);
        claims.put(USER_ID, adminDetails.getId());
        // 去重
        Set<PermissionDTO> permissionDTOs = new HashSet<>(adminDetails.getPermissionDTOs());
        String roles = JSONArray.toJSONString(permissionDTOs);
        claims.put(ROLE_ID, roles);
        return doGenerateToken(claims, adminDetails.getUsername(), tokenType);
    }

    /**
     * 获取权限信息
     *
     * @param jwtToken JWT token
     * @return 权限信息
     */
    public List<PermissionDTO> getPermissions(String jwtToken) {
        Claims claims = getAllClaimsFromToken(jwtToken);
        return getPermissions(claims);
    }

    /**
     * 获取权限信息
     *
     * @param claims JWT claims
     * @return 权限信息
     */
    public List<PermissionDTO> getPermissions(Claims claims) {
        return JSONObject.parseArray((String) claims.get(ROLE_ID), PermissionDTO.class);
    }

    public Long getUserId(String jwtToken) {
        Claims claims = getAllClaimsFromToken(jwtToken);
        try {
            return claims.get(USER_ID, Long.class);
        } catch (Exception e) {
            log.error("获取用户id失败, jwtToken: {}", jwtToken, e);
            return -1L;
        }
    }

    private String doGenerateToken(Map<String, Object> claims, String username,TokenType tokenType) {
        String expireTime = tokenType == TokenType.ACCESS ? expirationTime: expirationRefreshTime;
        // in second
        long expirationTimeLong = Long.parseLong(expireTime);

        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .setIssuer(tokenType.name())
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(getAllClaimsFromToken(token));
    }

    public boolean validateAccessToken(Claims claims){
        return TokenType.ACCESS.name().equals(claims.getIssuer());
    }

    public boolean validateRefreshToken(Claims claims){
        return TokenType.REFRESH.name().equals(claims.getIssuer());
    }

    public String createAccessTokenByRefreshToken(String refreshToken){
        Claims claims = getAllClaimsFromToken(refreshToken);
        if(validateRefreshToken(claims) && validateToken(refreshToken)){
            // in second
            long expirationTimeLong = Long.parseLong(expirationTime);
            final Date createdDate = new Date();
            final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);

            String username = claims.getSubject();
            Map<String, Object> newClaims = new HashMap<>(8);
            claims.forEach(newClaims::put);

            return Jwts.builder()
                    .setClaims(newClaims)
                    .setSubject(username)
                    .setIssuedAt(createdDate)
                    .setExpiration(expirationDate)
                    .setIssuer(TokenType.ACCESS.name())
                    .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(secret.getBytes()))
                    .compact();
        }
        return null;
    }

}
