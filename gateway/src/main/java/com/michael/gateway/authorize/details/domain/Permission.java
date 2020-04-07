package com.michael.gateway.authorize.details.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 权限表
 *
 * @author Michael Chu
 * @since 2020-04-02 11:28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission")
public class Permission extends BaseDomain {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("name")
    private String name;
    /** 访问资源地址 */
    @TableField("uri")
    private String uri;
    /**
     * 请求类型
     * {@link com.michael.gateway.authorize.details.domain.status.RequestMethodType}
     */
    @TableField("request_type")
    private Integer requestType;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    /** {@link com.michael.gateway.authorize.details.domain.status.CommonStatus} */
    @TableField("status")
    private Integer status;
}
