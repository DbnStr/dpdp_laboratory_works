package ru.bmstu.dpdp.lab_5;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class CacherActor extends AbstractActor {

    public static final float DEFAULT_NO_STORE_VALUE = -1.0f;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), self());

    private final Map<String, Float> cash = new HashMap<>();

    private void putIntoStore(StoreMessage message) {
        cash.put(message.getUrl(), message.getAvgRequestTime());
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(String.class, message -> sender().tell(cash.getOrDefault(message, DEFAULT_NO_STORE_VALUE), ActorRef.noSender()))
                .match(StoreMessage.class, this::putIntoStore)
                .matchAny(o -> log.info("recived unknown message"))
                .build();
    }
}
