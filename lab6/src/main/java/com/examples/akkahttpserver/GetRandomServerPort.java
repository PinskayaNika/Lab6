package com.examples.akkahttpserver;

class GetRandomServerPort {
    private String randomPort;

    GetRandomServerPort(String port) {
        this.randomPort = port;
    }

    String getRandomPort() {
        return this.randomPort;
    }
}
