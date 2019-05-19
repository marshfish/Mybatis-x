package com.mcode.mybatisx.dal.util;

@FunctionalInterface
public interface MapperEditor<T, R> {
    R edit(T t);
}
