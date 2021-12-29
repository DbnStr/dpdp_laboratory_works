package ru.bmstu.dpdp.lab_6;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import ru.bmstu.dpdp.lab_6.messages.GetRandomServerRequestMessage;
import ru.bmstu.dpdp.lab_6.messages.RandomServerMessage;
import ru.bmstu.dpdp.lab_6.messages.ServersListMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigStorageActor extends AbstractActor {

    private static final Logger logger = Logger.getLogger(ConfigStorageActor.class.getName());

    private ArrayList<String> servers;

    public ConfigStorageActor() {
        this.servers = new ArrayList<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ServersListMessage.class, this::saveServersList)
                .match(GetRandomServerRequestMessage.class, this::getRandomServerResponse)
                .matchAny(m -> logger.info("Unknown message" + m.toString()))
                .build();
    }

    private void getRandomServerResponse(GetRandomServerRequestMessage message) {
        getSender().tell(new RandomServerMessage(getRandomServer()), ActorRef.noSender());
    }

    private String getRandomServer() {
        return servers.get(getRandomInt(servers.size()));
    }

    private int getRandomInt(int end) {
        return (int) (Math.random() * end);
    }

    private void saveServersList(ServersListMessage message) {
        List<String> newServersList = message.getServers();
        logger.info("New servers list : " + newServersList);
        this.servers.clear();
        this.servers.addAll(newServersList);
    }

}
