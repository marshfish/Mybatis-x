package com.mcode.mybatisx.dal.core.executor;


import com.mcode.mybatisx.dal.entity.BaseDO;
import com.mcode.mybatisx.dal.entity.PagingDO;
import com.mcode.mybatisx.dal.entity.SaveResult;
import com.mcode.mybatisx.dal.util.EntityInjector;
import com.mcode.mybatisx.dal.util.Tuple;

import java.util.List;
import java.util.Map;

/**
 * 原生SQL执行器，将Sql发送到mapper执行
 * 可用于执行复杂查询
 */
public interface NativeSqlExecutor {

    /***
     * 求一个sql的查询总条数
     * @param sql sql
     * @param param 参数
     * @return 总条数
     */
    int count(String sql, Map<String, Object> param);

    /**
     * 求一个sql的查询总条数
     *
     * @param sql      sql
     * @param setParam 注入参数
     * @return 总条数
     */
    int count(String sql, EntityInjector<Map<String, Object>> setParam);

    /***
     * 原生查询sql
     * @param clazz 返回DO类型
     * @param sql sql
     * @param paramMap 参数map集合
     * @return return DO列表
     */
    <DATA extends BaseDO> List<DATA> select(Class<DATA> clazz, String sql, Map<String, Object> paramMap);

    /**
     * 原生查询sql
     *
     * @param clazz    返回DO类型
     * @param sql      sql
     * @param setParam 注入参数
     * @return DO列表
     */
    <DATA extends BaseDO> List<DATA> select(Class<DATA> clazz, String sql, EntityInjector<Map<String, Object>> setParam);

    /***
     * 分页查询
     * @param clazz class
     * @param toPage 所跳页
     * @param pageSize 每页条数
     * @param sql sql
     * @param param 查询参数
     * @return 带有分页信息的DO
     */
    <DATA extends BaseDO> PagingDO<DATA> selectPaging(Class<DATA> clazz, Integer toPage, Integer pageSize,
                                                      String sql, Map<String, Object> param);

    /**
     * 分页查询
     *
     * @param clazz    class
     * @param toPage   所跳页
     * @param pageSize 每页条数
     * @param sql      sql
     * @param setParam 注入参数
     * @return 带有分页信息的DO
     */
    <DATA extends BaseDO> PagingDO<DATA> selectPaging(Class<DATA> clazz, Integer toPage, Integer pageSize,
                                                      String sql, EntityInjector<Map<String, Object>> setParam);

    /**
     * 批量插入
     *
     * @param tableName  表名
     * @param fieldNames 字段名顺序列表，不可重复
     * @param values     二维数组，批量DO、DO字段值
     * @return 插入结果
     *
     */
    SaveResult insertBatch(String tableName, List<String> fieldNames, List<List<Tuple<Object, String>>> values);

    /***
     * 更新数据原生sql
     * @param sql sql
     * @param map 参数map集合
     * @return row
     */
    int update(String sql, Map<String, Object> map);

    /**
     * 更新数据原生sql
     *
     * @param sql      sql
     * @param setParam 参数map集合
     * @return row
     */
    int update(String sql, EntityInjector<Map<String, Object>> setParam);

    /***
     * 删除数据原生sql
     * @param sql sql
     * @param paramMap 条件
     * @return row
     */
    int delete(String sql, Map<String, Object> paramMap);

    /**
     * 删除数据原生sql
     *
     * @param sql      sql
     * @param setParam 注入参数
     * @return row
     */
    int delete(String sql, EntityInjector<Map<String, Object>> setParam);

    /**
     * 执行原生Sql
     *
     * @param sql sql
     * @return row
     */
    int executeNativeSql(String sql, Map<String, Object> paramMap);

    /**
     * 执行原生Sql
     * @param sql sql
     * @param setParam param
     * @return row
     */
    int executeNativeSql(String sql, EntityInjector<Map<String, Object>> setParam);


}
