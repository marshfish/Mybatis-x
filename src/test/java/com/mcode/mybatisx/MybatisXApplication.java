package com.mcode.mybatisx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@MapperScan(value = {"com.mcode.mybatisx.dal.mapper"})
public class MybatisXApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MybatisXApplication.class, args);


    }


}
