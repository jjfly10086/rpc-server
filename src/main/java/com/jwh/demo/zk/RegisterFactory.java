package com.jwh.demo.zk;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author jiangwenhua
 */
@ConfigurationProperties(prefix = "com.jwh.demo.register")
public class RegisterFactory {

    // 根节点名称
    private String servicesRootDir;

    // zk服务Host
    private String zkHost;

    // zk服务端口
    private Integer zkPort;

    // zk连接超时时间
    private Integer sessionTimeout;

    // 服务暴露端口
    private Integer exportPort;

    private static ZooKeeper zk;


    public RegisterFactory() {
    }

    public RegisterFactory(String servicesRootDir, String zkHost, Integer zkPort, Integer sessionTimeout, Integer exportPort) {
        this.servicesRootDir = servicesRootDir;
        this.zkHost = zkHost;
        this.zkPort = zkPort;
        this.sessionTimeout = sessionTimeout;
        this.exportPort = exportPort;
    }

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

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Integer getExportPort() {
        return exportPort;
    }

    public void setExportPort(Integer exportPort) {
        this.exportPort = exportPort;
    }

    public Integer getZkPort() {
        return zkPort;
    }

    public void setZkPort(Integer zkPort) {
        this.zkPort = zkPort;
    }

    private void connect(){
        // 创建一个与服务器的连接
        try{
            String zkUrl = zkHost + ":" +zkPort;
            zk = new ZooKeeper(zkUrl,sessionTimeout,null);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void createRoot(){
        try{
            // 创建一个目录节点
            if(zk.exists(servicesRootDir,false) == null){
                zk.create(servicesRootDir, null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }else{
                System.out.println(zk.getChildren(servicesRootDir,false));
            }
        }catch (KeeperException | InterruptedException e){
            e.printStackTrace();
        }
    }
    private void createServiceNode(String serviceName,String data){
        try{
            if(zk.exists(servicesRootDir+"/"+serviceName,false) == null){
                zk.create(servicesRootDir+"/"+serviceName,data.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }else{
                zk.setData(servicesRootDir+"/"+serviceName,data.getBytes(),-1);
                System.out.println(new String(zk.getData(servicesRootDir+"/"+serviceName,false,null)));
            }
        }catch (KeeperException | InterruptedException e){
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
    public void registerServices(Collection<String> interfaceNames, String data){
        connect();
        createRoot();
        for(String serviceName : interfaceNames){
            createServiceNode(serviceName,data);
        }
        closeConnect();
    }
    public void deleteServices(Collection<String> interfaceNames){
        try{
            connect();
            for(String serviceName : interfaceNames){
                if(zk.exists(servicesRootDir+"/"+serviceName,false) != null){
                    zk.delete(servicesRootDir+"/"+serviceName,-1);
                }
            }
        }catch (KeeperException | InterruptedException e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws UnknownHostException{
        RegisterFactory registerFactory = new RegisterFactory("/services", "10.10.10.34", 2181, 3000, 5001);
        ZkDataNode node = new ZkDataNode(InetAddress.getLocalHost().getHostAddress(), 8081);
        registerFactory.registerServices(Arrays.asList("com.jwh.demo.service.ITestInterface"), JSON.toJSONString(node));
//        registerFactory.deleteServices(Arrays.asList("com.jwh.demo.service.ITestInterface"));
    }
}
