package ru.bmstu.dpdp.lab_4.messages;

public class Get {

    private final String packageId;

    public Get(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageId() {
        return packageId;
    }
}
