package com.jwh.demo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
public class RemoteCallBody implements Serializable{

    private String hostName;
    private Integer port;
    private String serviceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] args;
    private String version;

    @Override
    public String toString() {
        return "hostName="+ hostName +"&port="+port+"&serviceName="+serviceName+"&methodName="+methodName+"&args="+args+"&version="+version;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
