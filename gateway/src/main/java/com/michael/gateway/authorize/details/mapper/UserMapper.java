package com.michael.gateway.authorize.details.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.michael.gateway.authorize.details.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Michael Chu
 * @since 2020-04-02 14:45
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过用户名称查找
     *
     * @param username 用户名称
     * @return
     */
    @Select("SELECT * FROM USER WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
}
