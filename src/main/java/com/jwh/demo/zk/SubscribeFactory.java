package com.jwh.demo.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
public class SubscribeFactory {
    private  String servicesRootDir;

    private   String zkUrl;

    private   Integer sessionTimeout;

    private static ZooKeeper zk;

    private static SubscribeFactory subscribeFactory;

    private SubscribeFactory(String servicesRootDir, String zkUrl, Integer sessionTimeout){
        this.servicesRootDir = servicesRootDir;
        this.zkUrl = zkUrl;
        this.sessionTimeout = sessionTimeout;
    }
    public  static SubscribeFactory newInstance(String servicesRootDir,String zkUrl,Integer sessionTimeout){
        if(subscribeFactory == null) {
            subscribeFactory = new SubscribeFactory(servicesRootDir, zkUrl, sessionTimeout);
        }
        return subscribeFactory;
    }
    private void connect(){
        // 创建一个与服务器的连接
        try{
            zk = new ZooKeeper(zkUrl,sessionTimeout,null);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void closeConnect(){
        try{
            zk.close();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public Map<String,Object> getServices(){
        Map<String,Object> serviceMap = new HashMap<>();
        connect();
        try{
            List<String> serviceList = zk.getChildren(servicesRootDir,false);
            for(String serviceName : serviceList){
                String data = new String(zk.getData(servicesRootDir+"/"+serviceName,false,null));
               serviceMap.put(serviceName,data);
            }
        }catch (KeeperException | InterruptedException e){
            e.printStackTrace();
        }
        closeConnect();
        return serviceMap;
    }

    public static void main(String[] args) throws UnknownHostException {
        SubscribeFactory subscribeFactory = SubscribeFactory.newInstance("/services","192.168.17.148:2181",3000);
        System.out.println(subscribeFactory.getServices());
    }
}
