package com.mcode.mybatisx.dal.core.domain;

import com.mcode.mybatisx.dal.entity.BaseDO;
import com.mcode.mybatisx.dal.entity.PagingDO;
import com.mcode.mybatisx.dal.util.EntityInjector;
import com.mcode.mybatisx.dal.util.EntitySupplier;

import java.util.List;

/**
 * 单表简单CURD API
 * @param <PK> 主键
 * @param <DATA> DO数据模型
 * @author hukaibo
 */
public interface CURDAccessLayer<PK, DATA extends BaseDO> {
    /**
     * 统计操作
     * 根据param参数的属性执行查询
     * @param param DO数据模型
     * @return 数量
     */
    int count(DATA param);

    /**
     * 统计操作
     * @param supplier 查询参数supplier
     * @return 数量
     */
    int count(EntityInjector<DATA> supplier);

    /**
     * 查询列表
     * 根据param参数的属性执行查询
     * @param param DO数据模型
     * @return DO列表
     */
    List<DATA> select(DATA param);

    /**
     * 查询列表
     * @param supplier 查询参数supplier
     * @return DO列表
     */
    List<DATA> select(EntityInjector<DATA> supplier);

    /**
     * 查询单条数据
     * 根据param参数的属性执行查询
     * @param param DO数据模型
     * @return DO模型
     */
    DATA selectOne(DATA param);

    /**
     * 查询单条数据
     * @param supplier 查询参数supplier
     * @return DO模型
     */
    DATA selectOne(EntityInjector<DATA> supplier);

    /**
     * 根据主键查询数据
     * @param id 主键id
     * @return DO模型
     */
    DATA selectById(PK id);

    /**
     * 根据主键列表查询多条数据
     * @param ids 主键id列表
     * @return DO列表
     */
    List<DATA> selectByIds(List<PK> ids);

    /**
     * 分页查询
     * @param param DO数据模型
     * @return 分页后的DO列表
     */
    PagingDO<DATA> selectPaging(DATA param);

    /**
     * 分页查询
     * @param supplier 查询参数supplier
     * @return 分页后的DO列表
     */
    PagingDO<DATA> selectPaging(EntityInjector<DATA> supplier);

    /**
     * 根据字段范围查询
     *
     * @param key    字段
     * @param values 范围
     * @return DO列表
     */
     List<DATA> selectIn(String key, List<Object> values);


    /**
     * 插入数据
     * @param param 插入参数DO
     * @return 受影响行数
     */
    int insert(DATA param);

    /**
     * 插入数据
     * @param supplier 插入参数DO supplier
     * @return 受影响行数
     */
    int insert(EntityInjector<DATA> supplier);

    /**
     * 批量插入数据
     * @param list 插入参数DO列表
     * @return 受影响行数
     */
    int insertBatch(List<DATA> list);

    /**
     * 批量插入数据
     * @param supplier 插入参数DO supplier
     * @return 受影响行数
     */
    int insertBatch(EntitySupplier<List<DATA>> supplier);

    /**
     * 更新单条数据
     * @param id 主键id
     * @param param 更新参数DO
     * @return 受影响行数
     */
    int update(PK id, DATA param);

    /**
     * 更新单条数据
     * @param id 主键id
     * @param supplier 更新参数supplier
     * @return 受影响行数
     */
    int update(PK id, EntityInjector<DATA> supplier);

    /**
     * 批量更新
     * @param idList 主键id列表
     * @param param 更新参数DO
     * @return 受影响行数
     */
    int updateBatch(List<PK> idList, DATA param);
    /**
     * 批量更新
     * @param idList 主键id列表
     * @param supplier 更新参数supplier
     * @return 受影响行数
     */
    int updateBatch(List<PK> idList, EntityInjector<DATA> supplier);

    /**
     * 插入数据
     * @param param 插入参数DO
     * @return 受影响行数
     */
    int delete(DATA param);

    /**
     * 删除数据
     * @param id 主键id
     * @return 受影响行数
     */
    int deleteById(PK id);

    /**
     * 批量删除
     * @param idList 主键id列表
     * @return 受影响行数
     */
    int deleteByIds(List<PK> idList);
}
