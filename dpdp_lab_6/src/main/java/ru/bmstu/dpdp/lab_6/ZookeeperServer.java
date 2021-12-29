package ru.bmstu.dpdp.lab_6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;
import ru.bmstu.dpdp.lab_6.messages.ServersListMessage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ZookeeperServer implements Watcher {

    private static final Integer ZOOKEEPER_TIMEOUT = 2000;
    private static final String SERVER_PATH = "/servers";
    private static final String DEFAULT_ZOOKEEPER_PATH = "127.0.0.1:2181";
    private static final Logger logger = Logger.getLogger(ConfigStorageActor.class.getName());

    private ZooKeeper zoo;
    private ActorRef storage;

    public ZookeeperServer(ActorRef storage) throws IOException {
        zoo = new ZooKeeper(DEFAULT_ZOOKEEPER_PATH, ZOOKEEPER_TIMEOUT, this);
        this.storage = storage;
    }

    public ZooKeeper getZoo() {
        return zoo;
    }

    public void startServer(String host, Integer port) throws InterruptedException, KeeperException {
        String name = host + ":" + port;
        String serverPath = zoo.create(SERVER_PATH + "/" + name,
                (host + ":" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        logger.info("Server connected, path : " + serverPath);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        List<String> serversList = null;
        try {
            serversList = zoo.getChildren(SERVER_PATH, this).stream()
                    .map(s -> SERVER_PATH + "/" + s)
                    .collect(Collectors.toList());
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        this.storage.tell(new ServersListMessage(serversList), ActorRef.noSender());
    }
}
