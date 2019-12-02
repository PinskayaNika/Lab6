package com.examples.akkahttpserver;

//    Разрабатываем akka http сервер который при получении запроса либо отправляет его
// на случайный сервер, уменьшая счетчик на 1. Либо осуществляет get для данного url и
// возвращает.

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;

public class AkkaHttpServer {
    private static ZooKeeper zooKeeper;
    private static int port;
    private static ActorRef storageActor;
    private static final String ROUTES = "routes";
    private static final String LOCALHOST = "localhost";
    private static final String SERVER_INFO = "Server online at http://localhost:";
    private static final String URL = "url";
    private static final String LOCALHOST = "localhost";


    public static void main (String[] args) throws IOException, KeeperException, InterruptedException {

        Scanner in = new Scanner(System.in);
        port = in.nextInt();

        ActorSystem system = ActorSystem.create(ROUTES);

        storageActor = system.actorOf(Props.create(StoreActor.class));

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

        final Http http = Http.get(system);

        final ActorMaterializer materializer = ActorMaterializer.create(system);

        AkkaHttpServer app = new AkkaHttpServer();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.route().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(LOCALHOST, port),
                materializer
        );

        System.out.println(SERVER_INFO + Integer.toString(port));
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Route route() {
        return concat(
                get(
                        () -> parameter(URL, url ->
                                parameter(COUNT, count -> {

                                }))
                )
        )
    }
}
