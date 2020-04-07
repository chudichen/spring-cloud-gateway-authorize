package com.michael.gateway.authorize.details.service;

import com.michael.gateway.authorize.details.domain.Permission;
import com.michael.gateway.authorize.details.domain.User;
import com.michael.gateway.authorize.details.domain.status.CommonStatus;
import com.michael.gateway.authorize.details.domain.status.RequestMethodType;
import com.michael.gateway.authorize.details.dto.PermissionDTO;
import com.michael.gateway.authorize.details.dto.UserPermissionDTO;
import com.michael.gateway.authorize.details.mapper.PermissionMapper;
import com.michael.gateway.authorize.details.mapper.UserMapper;
import com.michael.gateway.authorize.utils.EnumUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 通过用户名查找用户信息
 *
 * @author Michael.Chu
 * @date 2020/03/31
 */
@Slf4j
@Component
public class ReactAdminDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PermissionMapper permissionMapper;

    public ReactAdminDetailsServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.permissionMapper = permissionMapper;
    }

    /**
     * 通过username查找用户信息，以及用户权限信息
     *
     * @param username 用户名称
     * @return 用户权限信息
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("username: {}", username);
        User user = userMapper.findByUsername(username);
        String password = passwordEncoder.encode(user.getPassword());
        List<PermissionDTO> permissionDTOs = getPermissionDTOs(user.getId());
        UserPermissionDTO userPermissionDTO = new UserPermissionDTO(user.getId(), username, password, permissionDTOs);
        return Mono.just(userPermissionDTO);
    }

    /**
     * 通过userId来获取该用户的权限列表
     *
     * @param userId 用户id
     * @return {@link PermissionDTO}权限列表
     */
    private List<PermissionDTO> getPermissionDTOs(Long userId) {
        List<Permission> permissions = permissionMapper.getPermissionsByUserId(userId);
        return permissions.stream()
                .filter(this::validatePermission)
                .map(permission -> new PermissionDTO(permission.getName(), permission.getUri(),
                        getRequestMethodType(permission.getRequestType())))
                .collect(Collectors.toList());
    }

    /**
     * 通过序号获取{@link RequestMethodType}
     *
     * @param ordinal 数据库中的序号
     * @return {@link RequestMethodType}
     */
    private RequestMethodType getRequestMethodType(int ordinal) {
        return EnumUtils.getByNumber(RequestMethodType.class, ordinal);
    }

    /**
     * 验证权限是否有效：未被禁用，权限信息不为空
     *
     * @param permission {@link Permission}
     * @return {@code true} 表示正常
     */
    private boolean validatePermission(Permission permission) {
        if (Objects.equals(permission.getStatus(), CommonStatus.OFF.ordinal())) {
            return false;
        }

        return EnumUtils.getByNumber(RequestMethodType.class, permission.getRequestType()) != null;
    }
}
