package com.mcode.mybatisx.dal.annotation;

import java.lang.annotation.*;

/**
 * 用于标注DB字段名
 * 若字段设置该注解，则会解析字段名为下划线形式作为DB字段名
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldName {
    String value() default "";
}
