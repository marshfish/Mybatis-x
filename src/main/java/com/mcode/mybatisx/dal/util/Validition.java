package com.mcode.mybatisx.dal.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class Validition {
    private static  void throwIt(String desc){
        throw new IllegalArgumentException("query fail,param [ "+desc + " ] can not be null or empty");
    }
    public static void requireNonEmpty(String str, String desc) {
        if (StringUtils.isEmpty(str)) {
            throwIt(desc);
        }
    }

    public static <T>void requireNonEmpty(T str, String desc) {
        if (Objects.isNull(str)) {
            throwIt(desc);
        }
    }
    public static void requireNonEmpty(Collection<?> str, String desc) {
        if (CollectionUtils.isEmpty(str)) {
            throwIt(desc);
        }
    }

    public static void requireNonEmpty(Map<?,?> str, String desc) {
        if (CollectionUtils.isEmpty(str)) {
            throwIt(desc);
        }
    }

}
