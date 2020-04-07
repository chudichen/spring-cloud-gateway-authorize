package com.michael.gateway.authorize.details.domain.status;

/**
 * 登陆方式
 *
 * @author Michael.Chu
 * @date 2020/04/02
 */
public enum LoginType {

    /** web */
    WEB,
    /** 移动端 */
    MOBILE;

    public static LoginType defaultType(){
        return LoginType.WEB;
    }
}
