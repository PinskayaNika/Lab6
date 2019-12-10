package com.examples.akkahttpserver;
//
//import java.util.List;
//
//public class ServerMessage {
//    private List<String> serverList;
//
//    public ServerMessage(List<String> port) {
//        this.serverList = port;
//    }
//
//    public List<String> getServerPort() {
//        return this.serverList;
//    }
//}

import java.util.List;

public class ServerMessage {
    private List<String> serversList;

    public ServerMessage(List<String> port){
        this.serversList = port;
    }

    public List<String> getServerPort() {
        return this.serversList;
    }
}
