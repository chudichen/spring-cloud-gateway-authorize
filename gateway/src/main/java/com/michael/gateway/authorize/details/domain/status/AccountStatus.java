package com.michael.gateway.authorize.details.domain.status;


/**
 * 账号状态
 *
 * @author Michael.Chu
 * @date 2020/04/02
 */
public enum AccountStatus {
    /**
     * 初始化
     */
    INITIAL,
    /**
     * 正常
     */
    NORMAL,
    /**
     * 被锁定
     */
    LOCKED,
    /**
     * 被封禁
     */
    BANNED,
    /**
     * 不可用
     */
    DISABLE
}
