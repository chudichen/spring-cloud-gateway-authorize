package com.michael.gateway.authorize.details.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色表
 *
 * @author Michael Chu
 * @since 2020-04-02 11:28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("role")
public class Role extends BaseDomain {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("name")
    private String name;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    /** {@link com.michael.gateway.authorize.details.domain.status.CommonStatus} */
    @TableField("status")
    private Integer status;
}
