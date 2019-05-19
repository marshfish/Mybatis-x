package com.mcode.mybatisx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.mcode.mybatisx.dal.mapper"})
public class MybatisXApplication {
    public static void main(String[] args) { SpringApplication.run(MybatisXApplication.class, args);
    }


}
