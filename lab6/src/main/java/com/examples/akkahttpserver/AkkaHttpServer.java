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
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import javafx.util.Pair;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
//import org.omg.CORBA.TIMEOUT;

import java.io.IOException;
//import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

//import static java.util.stream.Stream.concat;

public class AkkaHttpServer extends AllDirectives {
    private static ZooKeeper zooKeeper;
    private static int port; //номер порта
    private static ActorRef storageActor;
    private static Http http;
    private static final String ROUTES = "routes";
    private static final String LOCALHOST = "localhost";
    private static final String SERVER_INFO = "Server online at http://localhost:";
    private static final String URL = "url";
    private static final String COUNT = "count";
    private static final String URL_ERROR_MESSAGE = "Unable to connect to url";
    private static final String NOT_FOUND = "404";
    private static final int TIMEOUT = 5000;


    public static void main (String[] args) throws IOException, KeeperException, InterruptedException {

        Scanner in = new Scanner(System.in);
        port = in.nextInt();

        ActorSystem system = ActorSystem.create(ROUTES);

        storageActor = system.actorOf(Props.create(StoreActor.class));

// подключение к зукиперу внутри программы
        zooKeeper = new ZooKeeper(
                "127.0.0.1:2181",
                TIMEOUT,//2000,
                a -> {}
                );
// zapuskaew odin raz potom kommentiw
        //постоянный
        //zooKeeper.create("/servers", Integer.toString(port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //временный , будет удаляться после завершения программы, пересоздается при перезапуске
        zooKeeper.create("servers/" + Integer.toString(port), Integer.toString(port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        zooKeeper.getChildren("/servers", a -> {
            // tut mi poluchaem dannie o tekuwix serverax
            List<String> servers = new ArrayList<>();
            try {
                servers = zooKeeper.getChildren("/servers", b -> {});
            } catch (KeeperException e) {
                e.printStackTrace();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(String s: servers){
                byte[] data = new byte[0];
                try {
                    data = zooKeeper.getData("/servers" + s, c -> {}, null);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print(data.toString());
                //System.out.print(zooKeeper.getData("/servers" + s, c -> {}, null).toString());
            }
        });

        http = Http.get(system);

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
//пример вызова http клиента
    private CompletionStage<HttpResponse> fetch(String url) {
        try {
            return http.singleRequest(
                    HttpRequest.create(url));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(HttpResponse.create().withEntity(NOT_FOUND));
        }
    }

    private CompletionStage<HttpResponse> fetchToServer (int port, String url, int parsedCount) {
        try {
            return http.singleRequest(
                    HttpRequest.create("http://localhost:" + Integer.toString(port) + "/?url=" + url + "&count=" +
                            Integer.toString(parsedCount - 1)));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(HttpResponse.create().withEntity(NOT_FOUND));
        }
    }

    private Route route() {
        return concat(
                get(
                        () -> parameter(URL, url ->
                                parameter(COUNT, count -> {
                                    int parsedCount = Integer.parseInt(count);
                                    if (parsedCount != 0) {
                                        CompletionStage<HttpResponse> response = Patterns.ask(storageActor, new GetRandomServerPort(Integer.toString(port)), java.time.Duration.ofMillis(TIMEOUT))
                                              .thenCompose(req ->
                                              fetchToServer((int) req, url,parsedCount)
                                              );
                                        return completeWithFuture(response);
                                    }
                                    try {
                                        return complete(fetch(url).toCompletableFuture().get());
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                        return complete(URL_ERROR_MESSAGE);
                                    }
                                }))
                )
        );
    }
}
