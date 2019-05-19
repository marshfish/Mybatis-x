package com.mcode.mybatisx.dal.core.executor;

import com.google.common.collect.Maps;
import com.mcode.mybatisx.dal.entity.BaseDO;
import com.mcode.mybatisx.dal.entity.PagingDO;
import com.mcode.mybatisx.dal.entity.SaveResult;
import com.mcode.mybatisx.dal.entity.template.SaveTemplate;
import com.mcode.mybatisx.dal.entity.template.SqlTemplate;
import com.mcode.mybatisx.dal.mapper.BaseMapper;
import com.mcode.mybatisx.dal.util.EntityInjector;
import com.mcode.mybatisx.dal.util.Tuple;
import com.mcode.mybatisx.dal.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mcode.mybatisx.dal.util.Validition.requireNonEmpty;


@Slf4j
public class MybatisNativeSqlExecutor implements NativeSqlExecutor {
    private static final String FROM = "FROM";
    private static final String SELECT_COUNT = "SELECT COUNT(1) ";
    private static final String LIMIT = " LIMIT ";
    private static final String COMMA = ",";
    @Resource
    private BaseMapper baseMapper;
    @Resource
    private ResultAssemble resultAssemble;
    @Resource
    private SqlFactory sqlFactory;

    @Override
    public <DATA extends BaseDO> List<DATA> select(Class<DATA> clazz, String sql, Map<String, Object> paramMap) {
        requireNonEmpty(clazz, "clazz");
        requireNonEmpty(sql, "sql");
        SqlTemplate sqlTemplate = new SqlTemplate();
        sqlTemplate.setSql(sqlFactory.getInSql(sql, paramMap));
        sqlTemplate.setParamMap(paramMap);
        List<Map<String, Object>> list = baseMapper.select(sqlTemplate);
        return resultAssemble.buildResultList(clazz, list);
    }

    @Override
    public <DATA extends BaseDO> List<DATA> select(Class<DATA> clazz, String sql, EntityInjector<Map<String, Object>> setParam) {
        return select(clazz, sql, injectParam(setParam));
    }

    private Map<String, Object> injectParam(EntityInjector<Map<String, Object>> setParam) {
        Map<String, Object> param = Maps.newHashMap();
        setParam.setValue(param);
        return param;
    }

    @Override
    public int count(String sql, Map<String, Object> param) {
        requireNonEmpty(sql, "sql");
        SqlTemplate sqlTemplate = new SqlTemplate();
        sqlTemplate.setSql(sqlFactory.getInSql(sql, param));
        sqlTemplate.setParamMap(param);
        return baseMapper.count(sqlTemplate);
    }

    @Override
    public int count(String sql, EntityInjector<Map<String, Object>> setParam) {
        return count(sql, injectParam(setParam));
    }


    @Override
    public <DATA extends BaseDO> PagingDO<DATA> selectPaging(Class<DATA> clazz, Integer toPage, Integer pageSize, String sql, Map<String, Object> param) {
        requireNonEmpty(sql, "sql");
        toPage = Util.getOrDefault(toPage, 1);
        pageSize = Util.getOrDefault(pageSize, 10);
        int total = this.count(SELECT_COUNT.concat(sql.substring(sql.indexOf(FROM))), param);
        sql = sql.concat(LIMIT).
                concat(String.valueOf(((toPage - 1) * pageSize))).
                concat(COMMA).
                concat(String.valueOf(pageSize));
        List<DATA> list = this.select(clazz, sql, param);
        PagingDO<DATA> pagingDO = new PagingDO<>();
        pagingDO.setList(list);
        pagingDO.setCurrentPage(toPage);
        pagingDO.setPageTotal((total % pageSize) > 0 ? total / pageSize + 1 : total / pageSize);
        pagingDO.setTotal(total);
        return pagingDO;
    }

    @Override
    public <DATA extends BaseDO> PagingDO<DATA> selectPaging(Class<DATA> clazz, Integer toPage, Integer pageSize, String sql, EntityInjector<Map<String, Object>> setParam) {
        return selectPaging(clazz, toPage, pageSize, sql, injectParam(setParam));
    }

    @Override
    public int executeNativeSql(String sql, Map<String, Object> paramMap) {
        requireNonEmpty(sql, "sql");
        SqlTemplate sqlTemplate = new SqlTemplate();
        sqlTemplate.setSql(sql);
        sqlTemplate.setParamMap(paramMap);
        return baseMapper.nativeSql(sqlTemplate);
    }

    @Override
    public int executeNativeSql(String sql, EntityInjector<Map<String, Object>> setParam) {
        return executeNativeSql(sql, injectParam(setParam));
    }

    @Override
    public int update(String sql, Map<String, Object> map) {
        requireNonEmpty(sql, "sql");
        requireNonEmpty(map, "map");
        SqlTemplate sqlTemplate = new SqlTemplate();
        sqlTemplate.setSql(sqlFactory.getInSql(sql, map));
        sqlTemplate.setParamMap(map);
        return baseMapper.update(sqlTemplate);
    }

    @Override
    public int update(String sql, EntityInjector<Map<String, Object>> setParam) {
        return update(sql, injectParam(setParam));
    }

    @Override
    public int delete(String sql, Map<String, Object> paramMap) {
        requireNonEmpty(sql, "sql");
        SqlTemplate sqlTemplate = new SqlTemplate();
        sqlTemplate.setSql(sqlFactory.getInSql(sql, paramMap));
        sqlTemplate.setParamMap(paramMap);
        return baseMapper.delete(sqlTemplate);
    }

    @Override
    public int delete(String sql, EntityInjector<Map<String, Object>> setParam) {
        return delete(sql, injectParam(setParam));
    }

    @Override
    public SaveResult insertBatch(String tableName, List<String> fieldNames, List<List<Tuple<Object, String>>> values) {
        //受限于mybatis，要批量返回主键，只能创建一堆sqlTemplate作为do
        List<SaveTemplate> list = values.stream().map(value -> {
            SaveTemplate saveDO = new SaveTemplate();
            saveDO.setSaveValues(value);
            return saveDO;
        }).collect(Collectors.toList());

        int row = baseMapper.insertBatch(tableName, fieldNames, list);
        List<Object> ids = list.stream().map(SaveTemplate::getId).collect(Collectors.toList());
        SaveResult saveResult = new SaveResult();
        saveResult.setInfluencesRow(row);
        saveResult.setIdList(ids);
        return saveResult;
    }
}
