package ru.bmstu.dpdp.lab_6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import org.asynchttpclient.AsyncHttpClient;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class AkkaHttpServer {

    private static final String SYS_NAME = "route";
    private static final String STARTED_MESSAGE = "AnonymizationServer started";
    public static final String STORAGE_ACTOR_NAME = "storage";

    private final Logger logger = Logger.getLogger(AkkaHttpServer.class.getName());
    private ActorRef storage;
    private String host;
    private Integer port;
    private ActorSystem system;
    private CompletionStage<ServerBinding> binding;

    ZookeeperServer zoo;


    public AkkaHttpServer(String host, Integer port) {
        this.host = host;
        this.port = port;
        this.system = ActorSystem.create(SYS_NAME);
    }

    public void start() throws IOException, InterruptedException, KeeperException {
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final AsyncHttpClient asyncHttpClient = asyncHttpClient();
        storage = system.actorOf(Props.create(ConfigStorageActor.class), STORAGE_ACTOR_NAME);

        zoo = new ZookeeperServer(storage);
        zoo.startServer(host, port);

        final RouteServer routeServer = new RouteServer(storage, asyncHttpClient, zoo.getZoo());

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                routeServer.createRoute().flow(system, materializer);

        binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                materializer
        );

        logger.info(STARTED_MESSAGE);
    }

    public void close() {
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

}
