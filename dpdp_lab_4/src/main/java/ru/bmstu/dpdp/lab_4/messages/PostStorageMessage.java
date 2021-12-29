package ru.bmstu.dpdp.lab_4.messages;

public class PostStorageMessage {
    private final String packageId, testId, result;

    public PostStorageMessage(String packageId, String testId, String result) {
        this.packageId = packageId;
        this.testId = testId;
        this.result = result;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getTestId() {
        return testId;
    }

    public String getResult() {
        return result;
    }
}
