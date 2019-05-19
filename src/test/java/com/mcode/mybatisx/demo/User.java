package com.mcode.mybatisx.demo;

import com.mcode.mybatisx.dal.annotation.TableName;
import com.mcode.mybatisx.dal.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("sys_user")
public class User extends BaseDO<Long> {
    private String name;
    private Integer age;
    private String email;
    private Long time;
    private String address;
    private String hobby;
    private Integer sex;
    private Long createTime;
    private Long updateTime;
}
