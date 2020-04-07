package com.michael.gateway.authorize.details.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

/**
 * @author Michael Chu
 * @since 2020-04-02 15:07
 */
@Data
public class RoleDTO implements Serializable {

    private Long id;
    private String name;
    private List<PermissionDTO> permissionModels;


}
