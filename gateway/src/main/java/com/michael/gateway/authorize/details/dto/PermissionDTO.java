package com.michael.gateway.authorize.details.dto;

import com.michael.gateway.authorize.details.domain.status.RequestMethodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限DTO
 *
 * @author Michael Chu
 * @since 2020-04-02 15:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO implements GrantedAuthority {

    /** 权限名字 */
    private String name;
    /** 访问资源地址 */
    private String uri;
    /** 请求类型 */
    private RequestMethodType requestType;

    @Override
    public String getAuthority() {
        return this.name;
    }

}
