package com.jwh.demo;


import com.alibaba.fastjson.JSON;
import com.jwh.demo.annotation.RpcProvider;
import com.jwh.demo.zk.RegisterFactory;
import com.jwh.demo.zk.ZkDataNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jiangwenhua
 */

@Configuration
@EnableConfigurationProperties({RegisterFactory.class})
public class ProviderFactory{

    @Autowired
    private RegisterFactory registerFactory;
    @Autowired
    private RpcContext rpcContext;

    private Set<String> interfaces;

    @PostConstruct
    public void init(){
        //扫描指定注解的类集合
        Map<String, Object> providerMap = rpcContext.getContext().getBeansWithAnnotation(RpcProvider.class);
        //从spring中获取对应类的实例
        interfaces = new HashSet<>(providerMap.size());
        for(Map.Entry<String, Object> entry : providerMap.entrySet()){
           Object obj = entry.getValue();
           String serviceName = obj.getClass().getInterfaces()[0].getName();
           interfaces.add(serviceName);
        }
        //注册服务
        try{
            if(!providerMap.isEmpty()){
                String host = InetAddress.getLocalHost().getHostAddress();
                Integer port = registerFactory.getExportPort();
                ZkDataNode dataNode = new ZkDataNode(host, port);
                registerFactory.registerServices(interfaces,JSON.toJSONString(dataNode));
                new Thread(()->{
                    //暴露接口实例
                    NIOServer.provide(providerMap.values(), port);
                }).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void destroy(){
        registerFactory.deleteServices(interfaces);
    }

}
