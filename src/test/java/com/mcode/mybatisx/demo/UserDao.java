package com.mcode.mybatisx.demo;

import com.mcode.mybatisx.dal.core.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.function.Supplier;

@Service
public class UserDao extends Repository<Long, User> {

    public void executeSql(Supplier<String> createSql) {
        createSql().executeNativeSql(createSql.get(),new HashMap<>());
    }

}