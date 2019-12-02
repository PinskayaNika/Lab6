package com.examples.akkahttpserver;

import java.util.List;

public class ServerMessage {
    private List<String> serverList;

    public ServerMessage(List<String> port) {
        this.serverList = port;
    }

    public List<String> getServerPort() {
        return this.serverList;
    }
}
