package com.mcode.mybatisx.dal.util;

public class Tuple<T, R> {
    private T first;
    private R second;

    public Tuple(T first, R second) {
        this.first = first;
        this.second = second;
    }

    public Tuple() {
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public R getSecond() {
        return second;
    }

    public void setSecond(R second) {
        this.second = second;
    }
}
