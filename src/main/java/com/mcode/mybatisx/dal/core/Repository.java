package com.mcode.mybatisx.dal.core;

import com.mcode.mybatisx.dal.core.domain.CURDRepository;
import com.mcode.mybatisx.dal.core.domain.DSLAccessLayer;
import com.mcode.mybatisx.dal.core.executor.NativeSqlExecutor;
import com.mcode.mybatisx.dal.entity.BaseDO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 继承该类，获取单表CURD操作api
 * 为保证DAO层的封装，仅允许在继承该类的子类中使用DSL查询与原生Sql查询api
 *
 * @param <PK>
 * @param <DATA>
 */
public abstract class Repository<PK, DATA extends BaseDO> extends CURDRepository<PK, DATA> {
    @Resource
    private DSLAccessLayer dslAccessLayer;
    @Resource
    private NativeSqlExecutor nativeSqlExecutor;

    protected DSLAccessLayer createDsl() {
        return dslAccessLayer.createDsl(parseClass());
    }

    protected NativeSqlExecutor createSql() {
        return nativeSqlExecutor;
    }
}
