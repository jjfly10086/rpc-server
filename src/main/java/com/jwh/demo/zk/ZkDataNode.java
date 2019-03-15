package com.jwh.demo.zk;

import java.io.Serializable;

public class ZkDataNode implements Serializable {

    private String host;

    private Integer port;

    public ZkDataNode() {
    }

    public ZkDataNode(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
