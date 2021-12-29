package ru.bmstu.dpdp.lab_4;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ru.bmstu.dpdp.lab_4.messages.Get;
import ru.bmstu.dpdp.lab_4.messages.PostStorageMessage;
import ru.bmstu.dpdp.lab_4.messages.TestResultMessage;

import java.util.HashMap;

public class StorageActor extends AbstractActor {

    public static final String UNKNOWN_MESSAGE = "unknown message";
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), self());

    private final HashMap<String, HashMap<String, String>> storage = new HashMap<>();

    private void store(PostStorageMessage message) {
        System.out.println(message.getPackageId());
        HashMap<String, String> testsResult = storage.get(message.getPackageId());
        if (testsResult == null) {
            HashMap<String, String> newTestsResult = new HashMap<>();
            newTestsResult.put(message.getTestId(), message.getResult());
            storage.put(message.getPackageId(), newTestsResult);
        } else {
            testsResult.put(message.getTestId(), message.getResult());
        }
    }

    private void sendResult(Get message) {
        String packageId = message.getPackageId();
        System.out.println("Send " + packageId);
        sender().tell(new TestResultMessage(packageId, storage.get(packageId)), getContext().parent());
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(PostStorageMessage.class, this::store)
                .match(Get.class, this::sendResult)
                .matchAny(o -> log.info(UNKNOWN_MESSAGE))
                .build();
    }
}
