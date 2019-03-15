package com.jwh.demo;

import com.alibaba.fastjson.JSON;
import com.jwh.demo.annotation.RpcReference;
import com.jwh.demo.zk.SubscribeFactory;
import com.jwh.demo.zk.ZkDataNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
@Configuration
@EnableConfigurationProperties({SubscribeFactory.class})
public class ReferenceFactory{

    @Autowired
    private SubscribeFactory subscribeFactory;
    @Autowired
    private RpcContext rpcContext;

    /**
     * 注入代理bean
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception{
        //获取zk注册的服务
        Map<String,Object> serviceMap = subscribeFactory.getServices();
        //Bean ID
        String[] beanNames = rpcContext.getContext().getBeanDefinitionNames();
        for(String beanId : beanNames){
            Object bean = rpcContext.getContext().getBean(beanId);
            Field[] fields = bean.getClass().getDeclaredFields();
            //实例化
            for(Field field : fields){
                RpcReference reference = field.getAnnotation(RpcReference.class);
                if(reference!=null){
                    String data = (String) serviceMap.get(field.getType().getName());
                    if(data == null){
                        throw new RuntimeException("no provider "+field.getType().getName()+" exists");
                    }
                    ZkDataNode dataNode = JSON.parseObject(data, ZkDataNode.class);
                    String hostName = dataNode.getHost();
                    Integer port = dataNode.getPort();
                    // 代理对象
                    Object value = NIOClient.refer(field.getType(),hostName,port);
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
