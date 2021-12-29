package ru.bmstu.dpdp.lab_5;

public class StoreMessage {

    private final String url;
    private final float avgRequestTime;

    public StoreMessage(String url, float avgRequestTime) {
        this.url = url;
        this.avgRequestTime = avgRequestTime;
    }

    public String getUrl() {
        return url;
    }

    public float getAvgRequestTime() {
        return avgRequestTime;
    }
}
