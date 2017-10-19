package com.jwh.demo;


import com.jwh.demo.annotation.RpcProvider;
import com.jwh.demo.service.ITestInterface;
import com.jwh.demo.service.impl.TestInterfaceImpl;
import com.jwh.demo.zk.RegisterFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
public class ProviderFactory implements ApplicationContextAware{

    private String zkUrl;
    private Integer zkSessionTimeout;
    private Integer providerPort;
    private String[] classNames;

    private static ApplicationContext applicationContext;

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }

    public void setZkSessionTimeout(Integer zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public void setProviderPort(Integer providerPort) {
        this.providerPort = providerPort;
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
        Set<String> serviceNames = new HashSet<>();
        for(String name : beanNames){
            Object obj = applicationContext.getBean(name);
            RpcProvider rpcProvider = obj.getClass().getAnnotation(RpcProvider.class);
            if(rpcProvider != null){
                services.add(obj);
                serviceNames.add(obj.getClass().getInterfaces()[0].getName());
            }
        }
        //注册服务
        RegisterFactory registerFactory = RegisterFactory.newInstance("/services",zkUrl, zkSessionTimeout);
        try{
            classNames = new String[serviceNames.size()];
            serviceNames.toArray(classNames);
            registerFactory.registerServices(classNames,InetAddress.getLocalHost().getHostAddress()+":"+ providerPort);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        //新开线程，避免阻塞spring容器启动；
        new Thread(()->{
            //暴露接口实例
            NIOServerTest.provide(services, providerPort);
        }).start();
    }

    public void destroy(){
        RegisterFactory registerFactory = RegisterFactory.newInstance("/services",zkUrl, zkSessionTimeout);
        registerFactory.deleteServices(classNames);
    }

}
