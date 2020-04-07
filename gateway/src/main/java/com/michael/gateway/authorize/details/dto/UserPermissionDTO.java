package com.michael.gateway.authorize.details.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.List;

/**
 * 用户DTO
 *
 * @author Michael Chu
 * @since 2020-04-02 14:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserPermissionDTO extends User implements Serializable {

    private Long id;
    private String username;
    private String password;
    /** 用户权限，由于组织，角色展开后的最终的细粒度权限 */
    private List<PermissionDTO> permissionDTOs;

    public UserPermissionDTO(Long id, String username, String password, List<PermissionDTO> permissionDTOs) {
        super(username, password, permissionDTOs);
        this.id = id;
        this.username = username;
        this.password = password;
        this.permissionDTOs = permissionDTOs;
    }
}
