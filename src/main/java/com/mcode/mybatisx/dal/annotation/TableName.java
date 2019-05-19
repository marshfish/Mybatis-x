package com.mcode.mybatisx.dal.annotation;

import java.lang.annotation.*;

/**
 * 用于标注数据库表名
 * 若DO未设置该注解，则会解析类名为下划线形式作为DB表名
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableName {
    String value() default "";
}
