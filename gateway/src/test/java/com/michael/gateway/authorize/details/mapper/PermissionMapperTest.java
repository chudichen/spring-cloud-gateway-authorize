package com.michael.gateway.authorize.details.mapper;

import com.michael.gateway.BaseTest;
import com.michael.gateway.authorize.details.domain.Permission;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 权限Mapper测试
 *
 * @author Michael Chu
 * @since 2020-04-02 17:48
 */
public class PermissionMapperTest extends BaseTest {

    @Autowired
    private PermissionMapper permissionMapper;

    @Test
    public void getPermissionsByUserId() {
        List<Permission> permissionsByUserId = permissionMapper.getPermissionsByUserId(1L);
        Assert.assertNotNull(permissionsByUserId);
    }
}