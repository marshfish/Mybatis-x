package com.mcode.mybatisx.dal.core.executor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.mcode.mybatisx.dal.entity.BaseDO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultAssemble {
    private static final String LOGGER = "logger";

    /**
     * 组装对象
     *
     * @param clazz class
     * @param list  参数列表
     * @return DO对象list
     */
    public <DATA extends BaseDO> List<DATA> buildResultList(Class<DATA> clazz, List<Map<String, Object>> list) {
        return list.stream().map(e -> getResult(clazz, e)).collect(Collectors.toList());
    }

    /***
     * 将map中的key的名称修改为根据某个标记编辑成驼峰的形式，并通过jsonUtil返回成class对象
     * @param map map
     * @return return
     */
    private <DATA extends BaseDO> DATA getResult(Class<DATA> clazz, Map<String, Object> map) {
        //去除三方插件的字段
        map.remove(LOGGER);
        return BeanUtil.fillBeanWithMap(map, ReflectUtil.newInstance(clazz), true);
    }
}
