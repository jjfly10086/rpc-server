package com.jwh.demo;

import com.jwh.demo.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
public class ReferenceFactory implements ApplicationContextAware{

    private String hostName;

    private Integer port;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 注入代理bean
     * @throws Exception
     */
    public void init() throws Exception{
        //Bean ID
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for(String beanId : beanNames){
            Object bean = applicationContext.getBean(beanId);
            Field[] fields = bean.getClass().getDeclaredFields();
            //实例化
            for(Field field : fields){
                RpcReference reference = field.getAnnotation(RpcReference.class);
                if(reference!=null){
                    Object value = NIOClientTest.refer(field.getType(),hostName,port);
                    if (value != null) {
                        if(!field.isAccessible()){
                            field.setAccessible(true);
                        }
                        field.set(bean, value);
                    }
                }
            }
        }
    }
}
