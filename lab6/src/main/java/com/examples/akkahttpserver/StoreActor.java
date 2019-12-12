package com.examples.akkahttpserver;
//
////а. создаем актор хранилище конфигурации.
////Он принимает две команды —
////-	список серверов (который отправит zookeeper watcher)
////-	запрос на получение случайного сервера
//
//import akka.actor.AbstractActor;
//import akka.actor.ActorRef;
//import akka.japi.pf.ReceiveBuilder;
////import scala.util.Random;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
////import java.util.*;
//
//public class StoreActor extends AbstractActor {
//    List<String> serversPortList = new ArrayList<>();
//
//    @Override
//    public Receive createReceive() {
//        return ReceiveBuilder.create()
//
//                //принимает список серверов (который отправит zookeeper watcher)
//                .match(ServerMessage.class, msg -> {
//
//                            for(String s : msg.getServerPort()){
//                                System.out.println(s);
//                            }
//
//                            serversPortList = msg.getServerPort();
//                        }
//                )
//
//                //принимает запрос на получение случайного сервера
//                .match(GetRandomServerPort.class, msg -> {
//                            Random rand = new Random();
//                            int length = serversPortList.size();
//                            int randIndx = rand.nextInt(length);
//                            while (serversPortList.get(randIndx).equals(msg.getRandomPort())) {
//                                randIndx = rand.nextInt(length);
//                            }
//                            getSender().tell(Integer.parseInt(serversPortList.get(randIndx)), ActorRef.noSender());
//                        }
//                ).build();
//    }
//}

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.examples.akkahttpserver.GetRandomServerPort;
import com.examples.akkahttpserver.ServerMessage;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class StoreActor extends AbstractActor {
    List<String> serversPortList;// = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()

                //принимает список серверов (который отправит zookeeper watcher)
                .match(
                        ServerMessage.class,
                        msg -> {

                            //вывод номеров подключенных портов
                            for(String s : msg.getServerPort()){
                                System.out.println(s);
                            }

                            serversPortList = msg.getServerPort();
                        })

                //принимает запрос на получение случайного сервера
                .match(
                        GetRandomServerPort.class,
                        msg -> {
                            Random rand = new Random();
                            int len = serversPortList.size();

                            System.out.println("leng" + len);

                            int rand_idx = rand.nextInt(len);
                            while (serversPortList.get(rand_idx).equals(msg.getRandomPort())) {
                                rand_idx = rand.nextInt(len);
                            }
                            getSender().tell(Integer.parseInt(serversPortList.get(rand_idx)), ActorRef.noSender());
                        })
                .build();
    }
}
