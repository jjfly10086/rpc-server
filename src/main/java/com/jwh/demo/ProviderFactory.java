package com.jwh.demo;


import com.jwh.demo.annotation.RpcProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
public class ProviderFactory implements ApplicationContextAware{

    private Integer port;

    private static ApplicationContext applicationContext;

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public  static <T> T getBean(Class<T> cls){
        return applicationContext.getBean(cls);
    }

    public void init(){
        //扫描指定注解的类集合
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        //从spring中获取对应类的实例
        Set<Object> services = new HashSet<>();
        for(String name : beanNames){
            Object obj = applicationContext.getBean(name);
            RpcProvider rpcProvider = obj.getClass().getAnnotation(RpcProvider.class);
            if(rpcProvider != null){
                services.add(obj);
            }
        }
        //新开线程，避免阻塞spring容器启动；
        new Thread(()->{
            //暴露接口实例
            NIOServerTest.provide(services,port);
        }).start();
    }
}
