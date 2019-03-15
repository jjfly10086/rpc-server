package com.jwh.demo;

import com.jwh.demo.service.ITestInterface;
import com.jwh.demo.service.ITestInterface2;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by Administrator
 */
public class NIOClient {

    public static Object nioClient(RemoteCallBody body) {
        Object result = null;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Selector selector = null;
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(body.getHostName(), body.getPort()));

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            Boolean isOver = false;
            while (!isOver) {
                selector.select();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isConnectable()) {
                        if (socketChannel.finishConnect()) {
                            byte[] bytes = ByteArrayUtils.objectToByteArray(body);
                            buffer.clear();
                            buffer.put(bytes);
                            buffer.flip();
                            while (buffer.hasRemaining()) {
                                System.out.println(buffer);
                                socketChannel.write(buffer);
                            }
                            key.interestOps(SelectionKey.OP_READ);//监听读就绪事件
                        }
                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        long bytesRead = sc.read(buf);
                        while (bytesRead > 0) {
                            bytesRead = sc.read(buf);
                        }
                        if (bytesRead == 0 | bytesRead == -1) {
                            isOver = true;
                            buf.flip();
                            result = ByteArrayUtils.byteArrayToObject(buf.array());
                            System.out.println("remote call return result = " + result);
                        }
                    }
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object remoteCall(RemoteCallBody body) {
        return nioClient(body);
    }

    /**
     * 返回代理对象
     *
     * @param interfaceClass
     * @param host
     * @param port
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T refer(Class<T> interfaceClass, String host, Integer port) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {
            RemoteCallBody body = new RemoteCallBody();
            body.setHostName(host);
            body.setPort(port);
            body.setServiceName(interfaceClass.getName());
            body.setParamTypes(method.getParameterTypes());
            body.setArgs(args);
            body.setMethodName(method.getName());
            return remoteCall(body);
        });
    }

    public static void main(String[] args) throws Exception {
        ITestInterface testInterface = refer(ITestInterface.class, "10.10.10.34", 8080);
        testInterface.getCurrentUserNum();
        ITestInterface2 testInterface2 = refer(ITestInterface2.class, "10.10.10.34", 8080);
        testInterface2.checkNum();
    }
}
