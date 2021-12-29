package ru.bmstu.dpdp.lab_6;

import akka.actor.ActorRef;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import ru.bmstu.dpdp.lab_6.messages.GetRandomServerRequestMessage;
import ru.bmstu.dpdp.lab_6.messages.RandomServerMessage;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

public class RouteServer extends AllDirectives {

    public static final String URL = "url";
    public static final String COUNT = "count";
    private ActorRef storage;
    private AsyncHttpClient httpClient;
    private ZooKeeper zooKeeper;
    private final Logger logger = Logger.getLogger(AkkaHttpServer.class.getName());

    public RouteServer(ActorRef storage, AsyncHttpClient httpClient, ZooKeeper zooKeeper) {
        this.storage = storage;
        this.httpClient = httpClient;
        this.zooKeeper = zooKeeper;
    }

    public Route createRoute() {
        return route(
                get(() -> parameter(URL,
                        url -> parameter(COUNT,
                                c -> get(url, Integer.parseInt(c)))))
        );
    }

    private Route get(String url, Integer count) {
        CompletionStage<Response> response;
        if (count == 0) {
            response = request(httpClient.prepareGet(url).build());
        } else {
            response = request(url, count - 1);
        }
        return completeOKWithFutureString(response.thenApply(Response::getResponseBody));
    }

    private CompletionStage<Response> request(Request req) {
        logger.info("Request " + req.getUrl());
        return httpClient.executeRequest(req).toCompletableFuture();
    }

    private CompletionStage<Response> request(String url, int count) {
        return Patterns.ask(storage, new GetRandomServerRequestMessage(), Duration.ofSeconds(5))
                .thenApply(o -> ((RandomServerMessage)o).getServerURL())
                .thenCompose(m -> request(getRequestForAnonimServer(getServerUrl(m), url, count)));
    }

    private Request getRequestForAnonimServer(String serverUrl, String purposeUrl, int count) {
        return httpClient.prepareGet("http://" + serverUrl)
                .addQueryParam("url", purposeUrl)
                .addQueryParam("count", Integer.toString(count))
                .build();
    }

    private String getServerUrl(String o) {
        try {
            return new String(zooKeeper.getData(o, false, null));
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}