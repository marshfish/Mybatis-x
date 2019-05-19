package com.mcode.mybatisx.dal.core.executor;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.mcode.mybatisx.dal.annotation.FieldName;
import com.mcode.mybatisx.dal.annotation.Ignore;
import com.mcode.mybatisx.dal.entity.BaseDO;
import com.mcode.mybatisx.dal.util.CiConsumer;
import com.mcode.mybatisx.dal.util.Tuple;
import com.mcode.mybatisx.dal.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlFactory {
    private static final String OPERATING_COUNT = " COUNT(*) ";
    private static final String OPERATING_ALL = " * ";
    private static final String UNDERLINE_T = "_";
    private static final String CACHE_FIELD_RENEW = "::";
    private static final String SELECT_FROM = "SELECT %s FROM %s WHERE 1 = 1 ";
    private static final String DELETE_FROM = "DELETE FROM %s WHERE 1 = 1 ";
    private static final String INDEX_T = "index";
    private static final String ARRAY_T = "#ARRAY";
    private static final int NEGATIVE_ONE = -1;
    private static final String SQL_T = "sql";
    private static final String COMMA = ", ";
    private static final String ID = "id";
    public static final String PARAM_MAP_T = "{paramMap.";
    private static final String leftBigBrackets = "{";
    private static final String rightBigBrackets = "}";
    private static final String mark = "#";
    private static final String point = ".";


    public <DATA extends BaseDO> String getSelectSql(String tableName, DATA entity) {
        String baseSql = String.format(SELECT_FROM, OPERATING_ALL, tableName);
        return baseSql + getWhereSql(entity);
    }

    public <DATA extends BaseDO> String getCountSql(String tableName, DATA entity) {
        String baseSql = String.format(SELECT_FROM, OPERATING_COUNT, tableName);
        return baseSql + getWhereSql(entity);
    }

    public <DATA extends BaseDO> String getDeleteSql(String tableName, DATA entity) {
        String baseSql = String.format(DELETE_FROM, tableName);
        String whereSql = getWhereSql(entity);
        if (StringUtils.isEmpty(whereSql)) {
            throw new RuntimeException("Unspecified delete range, this operation is too dangerous");
        }
        return baseSql + whereSql;
    }

    public String getUpdateSql(String tableName, Map<String, Object> updateSet) {
        StringBuilder sql = new StringBuilder("UPDATE ".concat(tableName).concat(" SET "));
        int size = updateSet.size();
        int i = 0;
        for (Map.Entry<String, Object> entry : updateSet.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String jdbcValue = appendJdbcType(value);
            sql.append("`").append(key).append("`=#{").append(key).append(jdbcValue).append("}")
                    .append(i != size - 1 ? COMMA : StringUtils.EMPTY);
            i++;
        }
        return sql.toString();
    }

    private <DATA extends BaseDO> String getWhereSql(DATA entity) {
        StringBuilder sqlBuilder = new StringBuilder();
        dynamicJdbcFieldOnMatch(entity, (javaField, realFieldStr, value) ->
                sqlBuilder.append(" AND ").
                        append(realFieldStr).
                        append("= #{").
                        append(javaField).
                        append("} "));
        return sqlBuilder.toString();
    }

    //DO字段(无父类、已修改@TableName字段名、含空字段)映射缓存
    private static Map<Class<? extends BaseDO>, List<String>> javaFieldMapping = Maps.newConcurrentMap();

    /**
     * 根据DO对象映射过去的jdbc字段名、DO对象中对应的字段值执行,且java值不为空时操作
     *
     * @param entity   DATA extends BaseDO
     * @param consumer 参数：java字段名、jdbc字段名、java值
     */
    public <DATA extends BaseDO> void dynamicJdbcFieldOnMatch(
            DATA entity, CiConsumer<String, String, Object> consumer) {
        //缓存DO的jdbc字段映射
        Class<? extends BaseDO> entityCls = entity.getClass();
        List<String> javaFieldMapping = SqlFactory.javaFieldMapping.computeIfAbsent(entityCls, aClass ->
                loadJavaFieldMapping(entityCls));
        javaFieldMapping.forEach(field -> {
            //获取原始字段值，构建sql
            String[] split = field.split(CACHE_FIELD_RENEW);
            String javaField = split[0];
            String decorateJavaField = split.length == 1 ? javaField : split[1];
            Object value;
            if ((value = ReflectUtil.getFieldValue(entity, javaField)) != null) {
                consumer.accept(javaField, StrUtil.toUnderlineCase(decorateJavaField), value);
            }
        });
    }

    private List<String> loadJavaFieldMapping(Class<? extends BaseDO> entityCls) {
        //过滤被@FieldName注解的字段
        Field[] fields = ReflectUtil.getFieldsDirectly(entityCls, false);
        Map<String, String> mapping = Stream.of(fields).
                map(this::collectChangeFields).
                filter(Objects::nonNull).
                collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond));
        //替换字段名、驼峰转下划线、过滤父类字段、缓存字段与@TableName映射关系
        BeanDesc beanDesc = BeanUtil.getBeanDesc(entityCls);
        return beanDesc.getProps().stream().map(propDesc -> {
            Field field = propDesc.getField();
            String fieldName = field.getName();
            if (fieldName.equals(ID)) {
                return mappingJavaField(mapping, fieldName);
            }
            //过滤除主键外的所有父类字段
            if (!field.getDeclaringClass().equals(entityCls)) {
                return null;
            }
            return mappingJavaField(mapping, fieldName);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private String mappingJavaField(Map<String, String> mapping, String fieldName) {
        String newFieldName;
        if ((newFieldName = mapping.get(fieldName)) != null) {
            return fieldName + CACHE_FIELD_RENEW + newFieldName;
        }
        return fieldName;
    }


    /**
     * 获取被FieldName.class注解的新旧字段名
     *
     * @param field
     * @return
     */
    private Tuple<String, String> collectChangeFields(Field field) {
        field.setAccessible(true);
        if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
            return null;
        }
        if (field.isAnnotationPresent(Ignore.class)) {
            return null;
        }
        if (field.isAnnotationPresent(FieldName.class)) {
            FieldName fieldNameAnno = field.getAnnotation(FieldName.class);
            String newName = fieldNameAnno.value();
            return new Tuple<>(field.getName(), newName);
        }
        return null;
    }

    /***
     * 获取IN操作的sql
     * @param map 参数集合
     * @param sql 参数
     * @return return
     */
    public String getInSql(String sql, Map<String, Object> map) {
        Map<String, Object> markMap = new HashMap<>();
        markMap.put(SQL_T, sql);
        markMap.put(INDEX_T, 0);
        return ((String) getInSql(map, markMap)
                .get(SQL_T))
                .replaceAll("[{]", PARAM_MAP_T)
                .replace(ARRAY_T, "");
    }


    /***
     * 获取IN操作的sql
     * @param map 参数集合
     * @param markMap 递归参数
     * @return return
     */
    private Map<String, Object> getInSql(Map<String, Object> map, Map<String, Object> markMap) {
        String sql = (String) markMap.get(SQL_T);
        int index = (int) markMap.get(INDEX_T);
        int indexIn = sql.indexOf(ARRAY_T, index);
        sql = Util.replaceRange(
                sql,
                point,
                UNDERLINE_T,
                leftBigBrackets, rightBigBrackets
        );
        replaceMapKeyName(map, point);
        if (indexIn == NEGATIVE_ONE) {
            markMap.put(SQL_T, sql);
            return markMap;
        }
        String keyStr = sql.substring(
                sql.indexOf(leftBigBrackets, indexIn),
                sql.indexOf(rightBigBrackets, indexIn) + 1
        );
        String key = keyStr.substring(keyStr.indexOf(leftBigBrackets) + 1, keyStr.indexOf(rightBigBrackets)).trim();
        Object[] objects = (Object[]) map.get(key);
        key = leftBigBrackets + key;
        StringBuilder inSql = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            inSql.append(mark).append(key).append("[").append(i).append("]").append(rightBigBrackets);
            if (i != objects.length - 1) {
                inSql.append(COMMA);
            }
        }
        sql = sql.replace(keyStr, inSql.toString());
        markMap.put(SQL_T, sql);
        markMap.put(INDEX_T, sql.indexOf(ARRAY_T, index) + ARRAY_T.length());

        getInSql(map, markMap);
        return markMap;
    }

    /***
     * 替换map中key的值，如果有变化则新put一个替换后的key value键值对
     * @param map map
     * @param oldChar oldChar
     */
    private void replaceMapKeyName(Map<String, Object> map, String oldChar) {
        Map<String, Object> newMap = new HashMap<>();
        if (map == null) return;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            key = key.replace(oldChar, SqlFactory.UNDERLINE_T);
            newMap.put(key, entry.getValue());
        }
        map.putAll(newMap);
    }


    public String appendJdbcType(Object object) {
        String type = ",jdbcType=";
        if (object instanceof String) {
            type += "VARCHAR";
        } else if (object instanceof Integer) {
            type += "INTEGER";
        } else if (object instanceof Long) {
            type += "BIGINT";
        } else if (object instanceof Date) {
            type += "TIMESTAMP";
        } else {
            type = "";
        }
        return type;
    }

    public String getWhereInSql(String sql, String keyName) {
        return sql.concat(" WHERE ").concat(keyName).concat(" IN(").concat(ARRAY_T).concat("{").concat(keyName).concat("})");
    }
}
