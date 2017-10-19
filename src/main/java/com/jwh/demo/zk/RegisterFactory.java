package com.jwh.demo.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/10/13 0013.
 */
public class RegisterFactory {

    private  String servicesRootDir;

    private   String zkUrl;

    private   Integer sessionTimeout;

    private static ZooKeeper zk;

    private static RegisterFactory registerFactory;

    private RegisterFactory(String servicesRootDir,String zkUrl,Integer sessionTimeout){
        this.servicesRootDir = servicesRootDir;
        this.zkUrl = zkUrl;
        this.sessionTimeout = sessionTimeout;
    }
    public  static RegisterFactory newInstance(String servicesRootDir,String zkUrl,Integer sessionTimeout){
        if(registerFactory == null) {
            registerFactory = new RegisterFactory(servicesRootDir, zkUrl, sessionTimeout);
        }
        return registerFactory;
    }
    private void connect(){
        // 创建一个与服务器的连接
        try{
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
    public void registerServices(String[] services,String data){
        connect();
        createRoot();
        for(String serviceName : services){
            createServiceNode(serviceName,data);
        }
        closeConnect();
    }
    public void deleteServices(String[] services){
        try{
            connect();
            for(String serviceName : services){
                if(zk.exists(servicesRootDir+"/"+serviceName,false) != null){
                    zk.delete(servicesRootDir+"/"+serviceName,-1);
                }
            }
        }catch (KeeperException | InterruptedException e){
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws UnknownHostException{
        RegisterFactory registerFactory = RegisterFactory.newInstance("/services","192.168.17.148:2181",3000);
        registerFactory.registerServices(new String[]{"com.jwh.demo.ITestInterface"}, InetAddress.getLocalHost().getHostAddress()+":"+8000);
        registerFactory.deleteServices(new String[]{"com.jwh.demo.ITestInterface"});
    }
}
