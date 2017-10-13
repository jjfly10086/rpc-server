package com.jwh.demo.service.impl;

import com.jwh.demo.RemoteCallBody;
import com.jwh.demo.service.ITestInterface;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
public class TestInterfaceImpl implements ITestInterface {
    @Override
    public RemoteCallBody getCurrentUserNum() {
        RemoteCallBody body = new RemoteCallBody();
        body.setMethodName("1111");
        body.setHostName("2121221111111111111111");
        body.setArgs(new Object[]{111,223,"33232"});
        body.setParamTypes(new Class[]{Integer.class});
        body.setServiceName("32322222222");
        body.setVersion("1.32323");
        body.setPort(111);
        return body;
    }
}
