package com.examples.akkahttpserver;

//а. создаем актор хранилище конфигурации.
//Он принимает две команды —
//-	список серверов (который отправит zookeeper watcher)
//-	запрос на получение случайного сервера

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;

public class StoreActor extends AbstractActor {
    List<String> serversPortList = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()

                //принимает список серверов (который отправит zookeeper watcher)
                .match(ServerMessage.class, msg -> {

                            for(String s : msg.getServerPort()){
                                System.out.println(s);
                            }

                            serversPortList = msg.getServerPort();
                        }
                )

                //принимает запрос на получение случайного сервера
                .match(GetRandomServerPort.class, msg -> {
                            Random rand = new Random();
                            int length = serversPortList.size();
                            int randIndx = rand.nextInt(length);
                            while (serversPortList.get(randIndx).equals(msg.getRandomPort())) {
                                randIndx = rand.nextInt(length);
                            }
                            getSender().tell(Integer.parseInt(serversPortList.get(randIndx)), ActorRef.noSender());
                        }
                ).build();
    }
}