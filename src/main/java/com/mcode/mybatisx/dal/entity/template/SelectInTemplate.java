package com.mcode.mybatisx.dal.entity.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class SelectInTemplate extends SqlTemplate {

    /***
     * 表名
     */
    private String tableName;

    /***
     * 数据库字段
     */
    private Object field;

    /***
     * 字段值集合
     */
    private List<? extends Object> fieldList;

}
