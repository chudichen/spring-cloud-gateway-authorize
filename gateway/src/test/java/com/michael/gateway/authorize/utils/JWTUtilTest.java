package com.michael.gateway.authorize.utils;

import com.michael.gateway.BaseTest;
import com.michael.gateway.authorize.details.domain.status.RequestMethodType;
import com.michael.gateway.authorize.details.dto.PermissionDTO;
import com.michael.gateway.authorize.details.dto.UserPermissionDTO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Michael Chu
 * @since 2020-04-03 18:00
 */
@Slf4j
public class JWTUtilTest extends BaseTest {

    @Autowired
    private JWTUtil jwtUtil;

    private String token = null;

    @Before
    public void prepare() {
        token = generateMockToken();
    }

    @Test
    public void getAllClaimsFromToken() {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        Assert.assertNotNull(token);
        log.info(claims.toString());
    }

    @Test
    public void getUsernameFromToken() {
    }

    @Test
    public void getExpirationDateFromToken() {
    }

    @Test
    public void generateToken() {
        Assert.assertNotNull(token);
    }

    @Test
    public void validateToken() {
    }

    @Test
    public void validateAccessToken() {
    }

    @Test
    public void validateRefreshToken() {
    }

    @Test
    public void createAccessTokenByRefreshToken() {
    }

    private String generateMockToken() {
        List<PermissionDTO> permissionDTOs = new ArrayList<>();
        permissionDTOs.add(new PermissionDTO("admin", "/admin", RequestMethodType.ALL));
        permissionDTOs.add(new PermissionDTO("user", "/user", RequestMethodType.ALL));
        UserPermissionDTO userPermissionDTO = new UserPermissionDTO(1L, "username", "password", permissionDTOs);
        return jwtUtil.generateToken(userPermissionDTO, TokenType.ACCESS, "id");
    }
}
