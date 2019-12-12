package com.examples.akkahttpserver;

import java.util.List;

class ServerMessage {
    private List<String> serverList;

    ServerMessage(List<String> port) {
        this.serverList = port;
    }

    List<String> getServerPort() {
        return this.serverList;
    }
}
