package com.mcode.mybatisx.dal.annotation;

import java.lang.annotation.*;

/***
 * 执行数据插入操作时，标有此注解的字段会被自动忽略
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Ignore{
}
