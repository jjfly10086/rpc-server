package com.jwh.demo;

import com.jwh.demo.annotation.RpcReference;
import com.jwh.demo.zk.SubscribeFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
public class ReferenceFactory implements ApplicationContextAware{

    private String zkUrl;
    private Integer zkSessionTimeout;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }

    public void setZkSessionTimeout(Integer zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    /**
     * 注入代理bean
     * @throws Exception
     */
    public void init() throws Exception{
        //获取zk注册的服务
        SubscribeFactory subscribeFactory = SubscribeFactory.newInstance("/services",zkUrl,zkSessionTimeout);
        Map<String,Object> serviceMap = subscribeFactory.getServices();
        //Bean ID
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for(String beanId : beanNames){
            Object bean = applicationContext.getBean(beanId);
            Field[] fields = bean.getClass().getDeclaredFields();
            //实例化
            for(Field field : fields){
                RpcReference reference = field.getAnnotation(RpcReference.class);
                if(reference!=null){
                    String data = (String) serviceMap.get(field.getType().getName());
                    if(data == null){
                        throw new RuntimeException("no provider "+field.getType().getName()+" exists");
                    }
                    String hostName = data.substring(0,data.indexOf(":"));
                    Integer port = Integer.parseInt(data.substring(data.indexOf(":")+1,data.length()));
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
