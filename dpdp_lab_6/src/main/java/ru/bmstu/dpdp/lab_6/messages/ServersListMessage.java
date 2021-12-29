package ru.bmstu.dpdp.lab_6.messages;

import java.util.ArrayList;
import java.util.List;

public class ServersListMessage {
    private List<String> servers;

    public ServersListMessage(List<String> servers) {
        this.servers = servers;
    }

    public List<String> getServers() {
        return servers;
    }
}
