package com.mcode.mybatisx.dal.util;

/**
 * 用于向实体类注入值，框架会使用这些值执行CURD操作
 * @param <T>
 */
@FunctionalInterface
public interface EntityInjector<T> {
    void setValue(T t);
}
