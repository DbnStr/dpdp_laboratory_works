package ru.bmstu.dpdp.lab_6;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class ZookeeperApp {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        if (args.length != 2) {
            System.out.println("Usage : ZookepeerApp <host> <port>");
            System.exit(-1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        AkkaHttpServer server = new AkkaHttpServer(host, port);
        server.start();
        System.in.read();
        server.close();
    }
}
