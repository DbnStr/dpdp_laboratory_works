package ru.bmstu.dpdp.lab_4;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.util.Timeout;
import ru.bmstu.dpdp.lab_4.messages.Get;
import ru.bmstu.dpdp.lab_4.messages.PackageData;
import scala.concurrent.Future;

import java.time.Duration;


import static akka.http.javadsl.server.Directives.*;

public class HttpRoute {

    public static final String SUCCESS_MESSAGE = "201 Created : Tests executed";
    private ActorRef router;
    private final static Timeout METHOD_TIMEOUT = Timeout.create(Duration.ofSeconds(3));

    public HttpRoute(ActorRef router) {
        this.router = router;
    }

    public Route createRoute() {
        return route(
                get(() -> parameter("packageId", (packageId) -> {
                    Future<Object> future = Patterns.ask(router, new Get(packageId), METHOD_TIMEOUT);
                    return completeOKWithFuture(future, Jackson.marshaller());
                })),
                post(() -> entity(Jackson.unmarshaller(PackageData.class), msg -> {
                    router.tell(msg, ActorRef.noSender());
                    return complete(SUCCESS_MESSAGE);
                }))
        );
    }
}
