package ru.bmstu.dpdp.lab_4.messages;

import java.util.HashMap;

public class TestResultMessage {
    private String packageId;
    private HashMap<String, String> testsResult;

    public TestResultMessage(String packageId, HashMap<String, String> testsResult) {
        this.packageId = packageId;
        this.testsResult = testsResult;
    }
}
