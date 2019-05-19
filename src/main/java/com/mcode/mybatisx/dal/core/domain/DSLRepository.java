package com.mcode.mybatisx.dal.core.domain;


import com.mcode.mybatisx.dal.entity.BaseDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class DSLRepository implements DSLAccessLayer {
    @Override
    public <DATA extends BaseDO> DSLAccessLayer createDsl(Class<DATA> parseClass) {
        return null;
    }

}
