package com.mcode.mybatisx.dal.entity.template;

import lombok.Data;

import java.util.Map;

@Data
public class SqlTemplate {
    /***
     * sql语句
     */
    private String sql;

    /***
     * 参数map集合
     */
    private Map<String, Object> paramMap;
}
