package com.mcode.mybatisx.dal.entity;

import cn.hutool.core.util.StrUtil;
import com.mcode.mybatisx.dal.annotation.Ignore;
import com.mcode.mybatisx.dal.annotation.TableName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

@Slf4j
public abstract class BaseDO<PK>{
    private PK id;
    @Ignore
    private static Class<?> clazz;
    @Ignore
    private static final String ORDER_BY = " ORDER BY ";
    @Ignore
    private Integer pageSize;
    @Ignore
    private Integer toPage;
    @Ignore
    private Integer page;
    @Ignore
    private String order;
    public String getTableName() {
        return getTableName(this.getClass());
    }

    public BaseDO() {
        Type superclass = this.getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) superclass;
        Type actualTypeArgument = type.getActualTypeArguments()[0];
        try {
            BaseDO.clazz = Class.forName(actualTypeArgument.getTypeName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends BaseDO> String getTableName(Class<T> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(TableName.class)).map(TableName::value).
                orElseGet(() -> {
                    String table = StrUtil.toUnderlineCase(clazz.getSimpleName());
                    log.info("cannot find annotation @TableName,will parse class name as table name:[{}]", table);
                    return table;
                });
    }

    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getToPage() {
        return toPage;
    }

    public void setToPage(Integer toPage) {
        this.toPage = toPage;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Class<?> getPKClass(){
        return clazz;
    }

    public String buildOrder(String sql) {
        return Optional.ofNullable(order).map(order -> sql + ORDER_BY + order + StringUtils.SPACE).
                orElse(sql);
    }
}
