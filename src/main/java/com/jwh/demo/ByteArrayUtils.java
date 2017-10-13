package com.jwh.demo;

import java.io.*;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
public class ByteArrayUtils {
    public static byte[] objectToByteArray(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] bytes = baos.toByteArray();
        oos.flush();
        oos.close();
        baos.close();
        return bytes;
    }
    public static Object byteArrayToObject(byte[] bytes){
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(bais);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("read object error");
        }
        Object obj = null;
        try{
            obj = ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return obj;
    }
}
