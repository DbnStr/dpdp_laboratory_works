package ru.bmstu.dpdp.lab_4;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;


public class TestJSApp {

    private static final String SYS_NAME = "JS_Test";
    private static final String ROUTER_ACTOR_NAME = "router";
    private static final Object LOG_OUT = System.out;
    private static final int PORT = 8833;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create(SYS_NAME);
        ActorRef actorRouter = system.actorOf(Props.create(RouterActor.class), ROUTER_ACTOR_NAME);
        LoggingAdapter logger = Logging.getLogger(system, LOG_OUT);
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        HttpRoute httpRoute = new HttpRoute(actorRouter);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = httpRoute.createRoute().flow(system, materializer);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOST, PORT),
                materializer
        );

        logger.info("SERVER ONLINE ON HOST : " + HOST + " PORT : " + PORT);
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }
}
