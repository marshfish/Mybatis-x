package com.mcode.mybatisx.dal.util;

/**
 * 用于提供实体类列表或其他类型，框架会使用这些值执行CURD操作
 * @param <T>
 */
@FunctionalInterface
public interface EntitySupplier<T> {
    T get();
}
