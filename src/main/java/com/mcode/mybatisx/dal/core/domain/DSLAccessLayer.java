package com.mcode.mybatisx.dal.core.domain;


import com.mcode.mybatisx.dal.entity.BaseDO;

/**
 * DSL风格查询API
 */
public interface DSLAccessLayer {

    <DATA extends BaseDO> DSLAccessLayer createDsl(Class<DATA> parseClass);
}
