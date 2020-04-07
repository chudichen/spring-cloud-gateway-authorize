package com.michael.gateway.authorize.details.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.michael.gateway.authorize.details.domain.Permission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限信息
 *
 * @author Michael Chu
 * @since 2020-04-02 14:47
 */
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 通过userId查找对应的权限
     *
     * @param userId 用户id
     * @return 权限列表
     */
    @Select("select distinct p.id, p.name, p.uri, p.request_type as requestType, " +
            "p.create_time as createTime, p.update_time as updateTime, p.status " +
            "from permission p left join role_permission rp on p.id = rp.permission_id " +
            "left join `role` r on rp.role_id = r.id " +
            "left join group_role gr on r.id = gr.role_id " +
            "left join `group` g on gr.group_id = g.id " +
            "left join user_group gu on g.id = gu.group_id " +
            "left join `user` u on gu.user_id = u.id " +
            "where u.id = #{userId}")
    List<Permission> getPermissionsByUserId(@Param("userId") Long userId);
}
