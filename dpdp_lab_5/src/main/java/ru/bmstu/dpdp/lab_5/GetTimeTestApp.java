package ru.bmstu.dpdp.lab_5;

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
import akka.http.javadsl.model.Query;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;

import static org.asynchttpclient.Dsl.asyncHttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class GetTimeTestApp {

    private static final String SYS_NAME = "tester";
    private static final String CACHER_ACTOR_NAME = "casher";
    private static final String HOST = "localhost";
    private static final String URL = "request_address";
    private static final String COUNT = "repeat";
    private static final int PORT = 8833;
    private static final Duration DELAY = Duration.ofSeconds(3);
    private static final Float NO_CACHE_VALUE = -1.0f;
    private static final Object LOG_PATH = System.out;
    public static final String DEFAULT_COUNT_OF_REQUST = "1";
    private static LoggingAdapter logger;

    public static void main(String[] args) throws IOException {
        System.out.println("Start testing");
        ActorSystem system = ActorSystem.create(SYS_NAME);
        logger = Logging.getLogger(system, LOG_PATH);
        ActorRef cacherActor = system.actorOf(Props.create(CacherActor.class), CACHER_ACTOR_NAME);
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = executeRequests(materializer, cacherActor);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOST, PORT),
                materializer
        );
        System.out.println("Server online at http://" + HOST + ":" + PORT);
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept((o) -> system.terminate());
    }

    private static Flow<HttpRequest, HttpResponse, NotUsed> executeRequests(ActorMaterializer materializer, ActorRef cacher) {
        return Flow.of(HttpRequest.class)
                .map(GetTimeTestApp::getRequestInfo)
                .mapAsync(8, (request) -> executeRequests(cacher, materializer, request))
                .map((r) -> {
                    cacher.tell(new StoreMessage(r.first(), r.second()), ActorRef.noSender());
                    return HttpResponse.create().withEntity("Result : " + r.first() + " , " + r.second()+ "\n");
                });
    }

    private static CompletionStage<Pair<String, Float>> executeRequests(ActorRef cacher,
                                                                        ActorMaterializer materializer,
                                                                        Pair<String, Integer> requestInfo) {
        return Patterns.ask(cacher, requestInfo.first(), DELAY)
                .thenApply(response -> (float)response)
                .thenCompose(response -> {
            if (!isCache(response)) {
                return Source.from(Collections.singleton(requestInfo))
                        .toMat(executeRequests(requestInfo.second()), Keep.right())
                        .run(materializer)
                        .thenApply(time -> {
                            float averageTime = (float) time / requestInfo.second();
                            logger.info("Get Request to " + requestInfo.first() + " Average time " + averageTime);
                            return new Pair<>(requestInfo.first(), averageTime);
                        });
            } else {
                return CompletableFuture.completedFuture(new Pair<>(requestInfo.first(), response));
            }
        });
    }

    private static Sink<Pair<String, Integer>, CompletionStage<Long>> executeRequests(Integer requestAmount) {
        return Flow.<Pair<String, Integer>>create()
                .mapConcat(pr -> new ArrayList<>(Collections.nCopies(pr.second(), pr.first())))
                .mapAsync(requestAmount, GetTimeTestApp::executeRequest)
                .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

    private static CompletableFuture<Long> executeRequest(String url) {
        AsyncHttpClient client = asyncHttpClient();
        long startTime = System.currentTimeMillis();
        CompletableFuture<Response> result = client.prepareGet(url).execute().toCompletableFuture();
        return result.thenCompose(response -> {
            long requestTime = System.currentTimeMillis() - startTime;
            logger.info("Get request " + url + " Time : " + requestTime);
            return CompletableFuture.completedFuture(requestTime);
        });
    }

    private static boolean isCache(float result) {
        return result != NO_CACHE_VALUE;
    }

    private static Pair<String, Integer> getRequestInfo(HttpRequest request) {
        Query query = request.getUri().query();
        String url = query.getOrElse(URL, HOST);
        int countOfRequests = Integer.parseInt(query.getOrElse(COUNT, DEFAULT_COUNT_OF_REQUST));
        return new Pair<>(url, countOfRequests);
    }
}