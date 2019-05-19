package com.mcode.mybatisx.dal.core.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mcode.mybatisx.dal.core.executor.SqlFactory;
import com.mcode.mybatisx.dal.core.executor.NativeSqlExecutor;
import com.mcode.mybatisx.dal.entity.BaseDO;
import com.mcode.mybatisx.dal.entity.PagingDO;
import com.mcode.mybatisx.dal.entity.SaveResult;
import com.mcode.mybatisx.dal.util.EntityInjector;
import com.mcode.mybatisx.dal.util.EntitySupplier;
import com.mcode.mybatisx.dal.util.Tuple;
import com.mcode.mybatisx.dal.util.Util;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.mcode.mybatisx.dal.util.Validition.requireNonEmpty;

@Slf4j
public class CURDRepository<PK, DATA extends BaseDO> implements CURDAccessLayer<PK, DATA> {
    private static final String ID_T = "id";
    private Class<DATA> clazz;
    private String tableName;
    @Resource
    private NativeSqlExecutor nativeSqlExecutor;
    @Resource
    private SqlFactory sqlFactory;

    @SuppressWarnings("unchecked")
    public CURDRepository() {
        Type superclass = this.getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) superclass;
        Type actualTypeArgument = type.getActualTypeArguments()[1];
        try {
            this.clazz = (Class<DATA>) Class.forName(actualTypeArgument.getTypeName());
            this.tableName = BaseDO.getTableName(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DATA> selectByIds(List<PK> ids) {
        requireNonEmpty(ids, "ids");
        return selectIn(ID_T, Lists.newArrayList(ids));
    }

    @Override
    public DATA selectById(PK id) {
        requireNonEmpty(id, "id");
        return selectIn(ID_T, Lists.newArrayList(id)).stream().findFirst().orElse(null);
    }

    @Override
    public List<DATA> select(DATA param) {
        requireNonEmpty(param, "param");
        String sql = sqlFactory.getSelectSql(tableName, param);
        Map<String, Object> beanToMap = BeanUtil.beanToMap(param, false, true);
        return nativeSqlExecutor.select(clazz, param.buildOrder(sql), beanToMap);
    }

    @Override
    public List<DATA> select(EntityInjector<DATA> supplier) {
        requireNonEmpty(supplier, "supplier");
        return select(setQuery(supplier));
    }

    @SneakyThrows
    private DATA setQuery(EntityInjector<DATA> supplier) {
        DATA entity = clazz.newInstance();
        supplier.setValue(entity);
        return entity;
    }

    @Override
    public DATA selectOne(DATA param) {
        return select(param).stream().findFirst().orElse(null);
    }

    @Override
    public DATA selectOne(EntityInjector<DATA> supplier) {
        requireNonEmpty(supplier, "supplier");
        return selectOne(setQuery(supplier));
    }

    @Override
    public int count(DATA param) {
        requireNonEmpty(param, "param");
        String sql = sqlFactory.getCountSql(tableName, param);
        Map<String, Object> paramMap = new HashMap<>();
        Util.copyMap(paramMap, param);
        return nativeSqlExecutor.count(sql, paramMap);
    }

    @Override
    public int count(EntityInjector<DATA> supplier) {
        requireNonEmpty(supplier, "supplier");
        return count(setQuery(supplier));
    }

    @Override
    public PagingDO<DATA> selectPaging(DATA param) {
        requireNonEmpty(param, "param");
        String sql = sqlFactory.getSelectSql(tableName, param);
        Map<String, Object> beanToMap = BeanUtil.beanToMap(param, false, true);
        return nativeSqlExecutor.selectPaging(clazz, param.getToPage(), param.getPageSize(),
                param.buildOrder(sql), beanToMap);
    }

    @Override
    public PagingDO<DATA> selectPaging(EntityInjector<DATA> supplier) {
        requireNonEmpty(supplier, "supplier");
        return selectPaging(setQuery(supplier));
    }

    @Override
    public List<DATA> selectIn(String key, List<Object> values) {
        requireNonEmpty(key, "key");
        requireNonEmpty(values, "values");
        String sql = "SELECT * FROM ".concat(tableName);
        String sqlIn = sqlFactory.getWhereInSql(sql, key);
        Map<String, Object> param = Maps.newHashMap();
        param.put(key, values.toArray());
        return nativeSqlExecutor.select(clazz, sqlIn, param);
    }


    @Override
    public int insertBatch(List<DATA> list) {
        requireNonEmpty(list, "list");
        SaveResult saveResult = insetBatch(list);
        list.forEach(data -> injectId(data, saveResult.nextId()));
        return saveResult.getInfluencesRow();
    }

    @Override
    public int insertBatch(EntitySupplier<List<DATA>> supplier) {
        requireNonEmpty(supplier, "supplier");
        return insertBatch(supplier.get());
    }

    @Override
    public int insert(DATA param) {
        requireNonEmpty(param, "param");
        SaveResult saveResult = insetBatch(Lists.newArrayList(param));
        injectId(param, saveResult.getFirst());
        return saveResult.getInfluencesRow();
    }

    @SuppressWarnings("unchecked")
    private void injectId(DATA object, Object id) {
        Class<?> pkCls = object.getPKClass();
        if (pkCls.equals(Long.class)) {
            object.setId(Long.valueOf(id.toString()));
        } else if (pkCls.equals(Integer.class)) {
            object.setId(Integer.valueOf(id.toString()));
        } else if (pkCls.equals(String.class)) {
            object.setId(id.toString());
        }
    }

    @Override
    public int insert(EntityInjector<DATA> supplier) {
        requireNonEmpty(supplier, "supplier");
        return insert(setQuery(supplier));
    }

    private int updateByKeys(List<PK> keyList, String keyName, Map<String, Object> updateSet) {
        updateSet.remove("id");
        String sql = sqlFactory.getUpdateSql(tableName, updateSet);
        updateSet.put(keyName, keyList.toArray());
        return nativeSqlExecutor.update(sqlFactory.getWhereInSql(sql, keyName), updateSet);
    }

    @Override
    public int update(PK id, DATA param) {
        requireNonEmpty(id, "id");
        requireNonEmpty(param, "param");
        Map<String, Object> resultMap = Maps.newHashMap();
        sqlFactory.dynamicJdbcFieldOnMatch(param, (javaField, jdbcField, javaValue) ->
                resultMap.put(jdbcField, javaValue));
        return this.updateByKeys(Lists.newArrayList(id), "id", resultMap);
    }

    @Override
    public int update(PK id, EntityInjector<DATA> supplier) {
        requireNonEmpty(supplier, "supplier");
        return update(id, setQuery(supplier));
    }

    @Override
    public int updateBatch(List<PK> idList, DATA param) {
        requireNonEmpty(idList, "id list");
        requireNonEmpty(param, "param");
        Map<String, Object> resultMap = Maps.newHashMap();
        sqlFactory.dynamicJdbcFieldOnMatch(param, (javaField, jdbcField, javaValue) ->
                resultMap.put(jdbcField, javaValue));
        return updateByKeys(idList, "id", resultMap);
    }

    @Override
    public int updateBatch(List<PK> idList, EntityInjector<DATA> supplier) {
        requireNonEmpty(supplier, "supplier");
        return updateBatch(idList, setQuery(supplier));
    }

    @Override
    public int delete(DATA param) {
        requireNonEmpty(param, "param");
        String deleteSql = sqlFactory.getDeleteSql(tableName, param);
        Map<String, Object> beanToMap = BeanUtil.beanToMap(param, false, true);
        return nativeSqlExecutor.delete(deleteSql, beanToMap);
    }

    @Override
    public int deleteByIds(List<PK> idList) {
        requireNonEmpty(idList, "idList");
        return this.removeByKeys(idList, "id", tableName);
    }

    private int removeByKeys(List<PK> keyList, String keyName, String tableName) {
        String sql = "DELETE FROM " + tableName;
        Map<String, Object> map = MapBuilder.create(new HashMap<String, Object>()).
                put(keyName, keyList.toArray()).build();
        return nativeSqlExecutor.delete(sqlFactory.getWhereInSql(sql, keyName), map);
    }

    @Override
    public int deleteById(PK id) {
        requireNonEmpty(id, "id");
        return this.deleteByIds(Lists.newArrayList(id));
    }


    private SaveResult insetBatch(List<DATA> list) {
        requireNonEmpty(list, "values");
        List<List<Tuple<Object, String>>> values = Lists.newArrayList();
        LinkedHashSet<String> fieldNames = Sets.newLinkedHashSet();
        //遍历DO list，设置jdbc字段名、注入字段值
        list.forEach(entity -> {
            List<Tuple<Object, String>> single = Lists.newArrayList();
            sqlFactory.dynamicJdbcFieldOnMatch(entity, (javaField, jdbcField, javaValue) -> {
                fieldNames.add(jdbcField);
                Tuple<Object, String> fieldTypeTuple = new Tuple<>(
                        javaValue, sqlFactory.appendJdbcType(javaValue));
                single.add(fieldTypeTuple);
            });
            values.add(single);
        });
        //若DO里字段均为空，不予处理
        if (CollectionUtils.isEmpty(fieldNames)) {
            log.warn(" Fields in DO are empty and will not be insert");
            return new SaveResult();
        }
        return nativeSqlExecutor.insertBatch(list.get(0).getTableName(), Lists.newArrayList(fieldNames), values);
    }
}
