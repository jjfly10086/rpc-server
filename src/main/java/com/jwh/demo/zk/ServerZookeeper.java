package com.jwh.demo.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/10/13 0013.
 */
public class ServerZookeeper {

    private    static ZooKeeper zk;

    public   static String zkUrl;

    public   static Integer timeout;

    public  static void zkConnect() throws IOException{
        zk = new ZooKeeper(zkUrl,timeout,null);
    }
    public  static void createNode(String serviceHost,Integer port,String serviceName) throws Exception{
        CountDownLatch connectedLatch = new CountDownLatch(1);
        if(zk.getState() == ZooKeeper.States.CONNECTING){
            connectedLatch.await();
        }
        if(zk.exists("/services",false) == null){
            zk.create("/services",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }
        zk.create("/services/",(serviceHost+":"+port+serviceName).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }
    public static void main(String[] args) throws Exception{
        zkUrl = "192.168.17.148:2181";
        timeout = 3000;
        zkConnect();
        createNode("localhost",8000,"ITestInterface");
    }
}
