package com.examples.akkahttpserver;

//а. создаем актор хранилище конфигурации.
//Он принимает две команды —
//-	список серверов (который отправит zookeeper watcher)
//-	запрос на получение случайного сервера

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StoreActor extends AbstractActor {
   List<String> serversPortList;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()

                //принимает список серверов (который отправит zookeeper watcher)
                .match(ServerMessage.class, msg -> {
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
                            Map<Integer, Integer> temp;
                            if (data.containsKey(msg.getURL())) {
                                temp = data.get(msg.getURL());
                            } else {
                                temp = new HashMap<>();
                            }
                            temp.put(msg.getCount(), msg.getTime());
                            data.put(msg.getURL(), temp);

                        }
                ).build();
    }
}