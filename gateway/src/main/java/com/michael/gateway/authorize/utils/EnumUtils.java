package com.michael.gateway.authorize.utils;

import org.springframework.lang.Nullable;

/**
 * 枚举的基本操作
 *
 * @author Michael Chu
 * @since 2019-08-15 10:15
 */
public interface EnumUtils {

    /**
     * 获取枚举对应的序号,未找到则返回{@code null}
     *
     * @param enumClass 类型
     * @param number 数字
     * @return {@code null}如果没有找到
     */
    @Nullable
    static <T extends Enum> T getByNumber(Class<? extends Enum> enumClass, int number) {
        try {
            // noinspection unchecked
            T[] enumConstants = (T[]) enumClass.getEnumConstants();
            for (T e: enumConstants) {
                if (e.ordinal() == number) {
                    return e;
                }
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
