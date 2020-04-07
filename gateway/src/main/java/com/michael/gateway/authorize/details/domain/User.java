package com.michael.gateway.authorize.details.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户表
 *
 * @author Michael Chu
 * @since 2020-04-02 11:28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class User extends BaseDomain {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    /**
     * 账号状态
     * {@link com.michael.gateway.authorize.details.domain.status.AccountStatus}
     */
    @TableField("status")
    private Integer status;
}
