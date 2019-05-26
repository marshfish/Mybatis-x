package com.mcode.mybatisx.dal.configuration;

import com.mcode.mybatisx.dal.core.domain.CURDRepository;
import com.mcode.mybatisx.dal.core.domain.DSLAccessLayer;
import com.mcode.mybatisx.dal.core.domain.DSLRepository;
import com.mcode.mybatisx.dal.core.executor.MybatisNativeSqlExecutor;
import com.mcode.mybatisx.dal.core.executor.NativeSqlExecutor;
import com.mcode.mybatisx.dal.core.executor.ResultAssemble;
import com.mcode.mybatisx.dal.core.executor.SqlFactory;
import com.mcode.mybatisx.dal.core.interceptor.DomainInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class BeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NativeSqlExecutor nativeSqlExecutor(){
        return new MybatisNativeSqlExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public DSLAccessLayer dslAccessLayer(){
        return new DSLRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResultAssemble resultAssemble(){
        return new ResultAssemble();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlFactory sqlFactory(){
        return new SqlFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public DomainInterceptor domainInterceptor(){
        return new DomainInterceptor();
    }
}
