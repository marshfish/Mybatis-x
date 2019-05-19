package com.mcode.mybatisx.dal.mapper;


import com.mcode.mybatisx.dal.entity.template.SaveTemplate;
import com.mcode.mybatisx.dal.entity.template.SelectInTemplate;
import com.mcode.mybatisx.dal.entity.template.SqlTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BaseMapper {
    List<Map<String, Object>> select(SqlTemplate SqlTemplate);

    int count(SqlTemplate SqlTemplate);

    int insertBatch(@Param("tableName")String tableName, @Param("fields") List<String> fields,
                    @Param("saveDo") List<SaveTemplate> saveDo);

    int update(SqlTemplate updateParamTemplate);

    int delete(SqlTemplate SqlTemplate);

    int nativeSql(SqlTemplate saveDO);
}

