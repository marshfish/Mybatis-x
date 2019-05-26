package com.mcode.mybatisx.dal.util;

import java.util.HashMap;

public class Maps<K, V> extends HashMap<K, V> {

    public Maps<K, V> puts(K k, V v) {
        put(k, v);
        return this;
    }

    public static <K, V> Maps<K, V> newHashMap(K k, V v) {
        return new Maps<K, V>().puts(k, v);
    }

    public static <K, V> Maps<K, V> newHashMap() {
        return new Maps<>();
    }
}
