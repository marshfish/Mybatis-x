package com.mcode.mybatisx.dal.entity.template;

import com.mcode.mybatisx.dal.util.Tuple;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class SaveTemplate extends SqlTemplate {
    /**
     * 主键
     */
    private Object id;
    /***
     * 保存值
     * 动态保存DO，受限于mybatis，要批量返回主键，不得不把SqlTemplate当作DO
     */
    private List<Tuple<Object, String>> saveValues;
}
