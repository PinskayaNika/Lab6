package com.examples.akkahttpserver;

//    Разрабатываем akka http сервер который при получении запроса либо отправляет его
// на случайный сервер, уменьшая счетчик на 1. Либо осуществляет get для данного url и
// возвращает.

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class AkkaHttpServer {
    private static ZooKeeper zooKeeper;
    private static final String ROUTES = "routes";
    private static final String LOCALHOST = "localhost";
    private static final String SERVER_INFO = "Server online at http://localhost:8080/\nPress RETURN to stop...";

    public static void main (String[] args) throws IOException, KeeperException, InterruptedException {

        
// подключение к зукиперу внутри программы
        zooKeeper = new ZooKeeper(
                "127.0.0.1:2181",
                2000,
                a -> {}
                );
// zapuskaew odin raz potom kommentiw
        //постоянный
        zooKeeper.create("/servers", "server".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //временный , будет удаляться после завершения программы, пересоздается при перезапуске
        zooKeeper.create("/servers/" + PORT_NUMBER, PORT_NUMBER.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        zooKeeper.getChildren("/servers", a -> {
            // tut mi poluchaem dannie o tekuwix serverax
            List<String> servers = new ArrayList<>();
            try {
                servers = zooKeeper.getChildren("/servers", b -> {});
            } catch (KeeperException e) {


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(String s: servers){
                System.out.print(zooKeeper.getData("/servers" + s, c -> {}, null).toString());
            }
        });
    }
}
