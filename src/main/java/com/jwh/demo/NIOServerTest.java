package com.jwh.demo;

import com.jwh.demo.service.ITestInterface;
import com.jwh.demo.service.ITestInterface2;
import com.jwh.demo.service.impl.TestInterfaceImpl;
import com.jwh.demo.service.impl.TestInterfaceImpl2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator
 */
public class NIOServerTest {

    private static final int BUF_SIZE=1024;
    private static final int PORT = 8080;
    private static final int TIMEOUT = 1000;

    /**
     * 读取信息并执行
     * @param key
     */
    public static void handleRead(SelectionKey key,Set<Object> serviceImpls){
        SocketChannel sc = (SocketChannel)key.channel();
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        long bytesRead = 0;
        try{
            bytesRead = sc.read(buf);
            if(bytesRead>0){
                RemoteCallBody body = (RemoteCallBody) ByteArrayUtils.byteArrayToObject(buf.array());
                System.out.println("remote address:"+sc.getRemoteAddress()+"&"+body.toString());
                //调用
                Class cls = Class.forName(body.getServiceName());
                Method method = cls.getMethod(body.getMethodName(),body.getParamTypes());
                Object realCallObj = chooseCallService(serviceImpls,cls);
                Object result = method.invoke(realCallObj,body.getArgs());
                //写回
                handleWrite(key,result);
            }
            System.out.println();
        }catch (IOException | ClassNotFoundException | NoSuchMethodException
                | IllegalAccessException | InvocationTargetException e){
            try {
                sc.close();
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }
    }

    /**
     * 写回调用结果
     * @param key
     */
    public static void handleWrite(SelectionKey key,Object result){
        SocketChannel sc = (SocketChannel)key.channel();
        try {
            //返回信息
            byte[] returnContent = ByteArrayUtils.objectToByteArray(result);
            ByteBuffer writeBuf = ByteBuffer.allocate(returnContent.length);
            writeBuf.put(returnContent);
            writeBuf.flip();
            sc.write(writeBuf);
            System.out.println("remote call result = "+result);
            //切换事件
            sc.register(key.selector(),SelectionKey.OP_WRITE);
        }catch (IOException e){
            try {
                sc.close();
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }
    }

    /**
     * 判断远端需要调用的哪个实例
     * @param serviceImpls
     * @param interfaceClass
     * @return
     */
    public static Object chooseCallService(Set<Object> serviceImpls, Class<?> interfaceClass){
        for(Object obj : serviceImpls){
           if(interfaceClass.isInstance(obj)){
               return obj;
           }
        }
        throw new RuntimeException("provider not exists");
    }
    /**
     * 暴露接口
     * @param serviceImpls
     * @param port
     */
    public static void provide(Set<Object> serviceImpls, Integer port) {
        nioServerStart(serviceImpls,port);
    }

    /**
     * nio server启动
     * @param serviceImpls
     * @param port
     */
    public static void nioServerStart(Set<Object> serviceImpls,Integer port){
        Selector selector = null;
        ServerSocketChannel ssc = null;
        try{
            selector = Selector.open();
            ssc= ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(port));
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while(true){
//                selector.select(1000);
                if(selector.select(1000) == 0){
                    System.out.println("ready accept remote call -----");
                    continue;
                }
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    iter.remove();
                    if(key.isAcceptable()){
                        System.out.println("handle Accept");
                        ServerSocketChannel ssChannel = (ServerSocketChannel)key.channel();
                        SocketChannel sc = ssChannel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector,SelectionKey.OP_READ);
                    }
                    if(key.isReadable()){
                        System.out.println("handle Read");
                        handleRead(key,serviceImpls);
                    }

                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(selector!=null){
                    selector.close();
                }
                if(ssc!=null){
                    ssc.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        ITestInterface testInterface = new TestInterfaceImpl();
        ITestInterface2 testInterface2 = new TestInterfaceImpl2();
        Set<Object> services = new HashSet<>();
        services.add(testInterface);
        services.add(testInterface2);

        //新开线程，避免阻塞spring容器启动；
        new Thread(()->{
            //暴露接口实例
            provide(services,PORT);
        }).start();
    }
}
