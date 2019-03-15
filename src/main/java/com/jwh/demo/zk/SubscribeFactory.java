package com.jwh.demo.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangwenhua
 */
@ConfigurationProperties(prefix = "com.jwh.demo.subscribe")
public class SubscribeFactory {

    private String servicesRootDir;

    private String zkHost;

    private Integer zkPort;

    private Integer sessionTimeout;

    private static ZooKeeper zk;

    public String getServicesRootDir() {
        return servicesRootDir;
    }

    public String getZkHost() {
        return zkHost;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setServicesRootDir(String servicesRootDir) {
        this.servicesRootDir = servicesRootDir;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public Integer getZkPort() {
        return zkPort;
    }

    public void setZkPort(Integer zkPort) {
        this.zkPort = zkPort;
    }

    public static ZooKeeper getZk() {
        return zk;
    }

    public static void setZk(ZooKeeper zk) {
        SubscribeFactory.zk = zk;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public SubscribeFactory() {
    }

    private SubscribeFactory(String servicesRootDir, String zkHost, Integer zkPort, Integer sessionTimeout){
        this.servicesRootDir = servicesRootDir;
        this.zkHost = zkHost;
        this.zkPort = zkPort;
        this.sessionTimeout = sessionTimeout;
    }

    private void connect(){
        // 创建一个与服务器的连接
        try{
            String zkUrl = zkHost + ":" + zkPort;
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
        SubscribeFactory subscribeFactory = new SubscribeFactory("/services","192.168.17.148",2181,3000);
        System.out.println(subscribeFactory.getServices());
    }
}
