package com.mcode.mybatisx.dal.core.interceptor;

import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.stream.Collectors;

public class DomainInterceptor implements ApplicationContextAware {
    private static List<DomainInterceptor> list = Lists.newLinkedList();


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        List<DomainInterceptor> interceptors = context.getBeansOfType(DomainInterceptor.class).
                values().
                stream().
                filter(v -> !v.getClass().equals(DomainInterceptor.class)).
                collect(Collectors.toList());
        list.addAll(interceptors);
    }

    public void beforeExecute() {

    }

    public void afterReturn() {

    }

}
