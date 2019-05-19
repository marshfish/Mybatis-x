package com.mcode.mybatisx.dal.util;

@FunctionalInterface
public interface CiConsumer<T, Y, U> {
    void accept(T t, Y y, U u);
}
