package com.examples.akkahttpserver;

//    Разрабатываем akka http сервер который при получении запроса либо отправляет его
// на случайный сервер, уменьшая счетчик на 1. Либо осуществляет get для данного url и
// возвращает.

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class AkkaHttpServer {


    public static void main (String[] args) throws IOException {



        final Http http = Http.get(context().system());

        CompletionStage<HttpResponse> fetch(String url) {
            return;http.singleRequest(HttpRequest.create(url));
        }
    }
}
