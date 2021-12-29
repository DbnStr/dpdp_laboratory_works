package ru.bmstu.dpdp.lab_6.messages;

public class RandomServerMessage {
    private String serverURL;

    public RandomServerMessage(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getServerURL() {
        return serverURL;
    }
}
