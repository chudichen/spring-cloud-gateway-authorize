package com.michael.gateway.authorize.details.domain;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Michael Chu
 * @since 2020-04-02 10:10
 */
public abstract class BaseDomain implements Serializable {

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
