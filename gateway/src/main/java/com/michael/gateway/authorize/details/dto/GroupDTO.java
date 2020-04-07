package com.michael.gateway.authorize.details.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.naming.Name;
import java.util.List;

/**
 * @author Michael Chu
 * @since 2020-04-02 15:05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupDTO {

    private Long id;
    private Name name;
    private List<RoleDTO> roleModels;
}
