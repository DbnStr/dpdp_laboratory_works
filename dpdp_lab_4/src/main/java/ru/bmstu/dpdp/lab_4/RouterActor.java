package ru.bmstu.dpdp.lab_4;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import ru.bmstu.dpdp.lab_4.messages.ExecuteMessage;
import ru.bmstu.dpdp.lab_4.messages.Get;
import ru.bmstu.dpdp.lab_4.messages.PackageData;
import ru.bmstu.dpdp.lab_4.messages.TestData;

import java.util.ArrayList;

public class RouterActor extends AbstractActor {

    public static final String UNKNOWN_MESSAGE = "unknown message";
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), self());
    private ActorRef storageActor;
    private final String ACTOR_STORAGE_NAME = "test_results_storage";
    private Router router;

    public RouterActor() {
        storageActor = getContext().actorOf(Props.create(StorageActor.class), ACTOR_STORAGE_NAME);
        getContext().watch(storageActor);

        ArrayList<Routee> routees = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            ActorRef execActor = getContext().actorOf(Props.create(TesterActor.class));
            getContext().watch(execActor);
            routees.add(new ActorRefRoutee(execActor));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(PackageData.class, this::runTests)
                .match(Get.class, message -> {
//                    System.out.println("route get");
                    storageActor.tell(message, sender());
                })
                .matchAny(o -> log.info(UNKNOWN_MESSAGE))
                .build();
    }

    private void runTests(PackageData data) {
        for(TestData test : data.getTests()) {
//            System.out.println("Run test");
            router.route(ExecuteMessage.getExecuteMessage(data, test), storageActor);
        }
    }
}
