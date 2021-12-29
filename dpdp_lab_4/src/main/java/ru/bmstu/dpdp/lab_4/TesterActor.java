package ru.bmstu.dpdp.lab_4;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ru.bmstu.dpdp.lab_4.messages.ExecuteMessage;
import ru.bmstu.dpdp.lab_4.messages.PostStorageMessage;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class TesterActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), self());

    private String execute(ExecuteMessage message) {
        String result;
        try {
            ScriptEngine e = new ScriptEngineManager().getEngineByName("nashorn");
            e.eval(message.getJsScript());
            Invocable in = (Invocable) e;
            result = in.invokeFunction(message.getFunctionName(), message.getParams().toArray()).toString();
        } catch (Exception e) {
            return message.getPackageId() + " ERROR " + e.getMessage();
        }
        if (result.equals(message.getExpectedResult())) {
            return "OK packageId : " + message.getPackageId() + " result : " + result;
        } else {
            return "FAIL packageId : " + message.getPackageId() + " result : " + result;
        }
    }

    private void saveIntoStorage(ExecuteMessage message) {
        sender().tell(new PostStorageMessage(message.getPackageId(), message.getTestName(), execute(message)), self());
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(ExecuteMessage.class, this::saveIntoStorage)
                .matchAny(o -> log.info("Unknown message " + o.toString()))
                .build();
    }
}
