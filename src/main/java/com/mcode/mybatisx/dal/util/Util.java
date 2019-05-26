package com.mcode.mybatisx.dal.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Slf4j
public class Util {

    /***
     * 讲一个字符串中两个字符之间的值替换为其他值
     * @param source 数据源
     * @param oldChar 要替换的字符串原来的值
     * @param newChar 要替换的字符串最新的值
     * @param start    范围开始的值
     * @param end 范围结束的值
     * @return
     * select * from user where user = #{user}
     */
    public static String replaceRange(String source, String oldChar, String newChar, String start, String end) {
        return replaceRange(source, oldChar, newChar, start, end, 0);
    }

    private static String replaceRange(String source, String oldChar, String newChar, String start, String end, int index) {
        int startInt = source.indexOf(start, index);
        int endInt = source.indexOf(end, index);
        if (endInt == -1)
            return source;
        return replaceRange(
                source.substring(0, startInt) + source.substring(startInt, endInt).replace(oldChar, newChar) + source.substring(endInt, source.length()),
                oldChar,
                newChar,
                start,
                end,
                endInt + 1
        );
    }


    public static <T>T getOrDefault(T value, T defaultV) {
        return value == null ? defaultV : value;
    }



}
