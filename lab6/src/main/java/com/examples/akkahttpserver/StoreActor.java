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
                .match(TestingResult.class, msg -> {
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