package ru.bmstu.dpdp.lab_4.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class PackageData {
    @JsonProperty("packageId")
    private final String packageId;
    @JsonProperty("jsScript")
    private final String jsScript;
    @JsonProperty("functionName")
    private final String functionName;
    @JsonProperty("tests")
    private final ArrayList<TestData> tests;

    public PackageData(@JsonProperty("packageId") String packageId,
                       @JsonProperty("jsScript") String jsScript,
                       @JsonProperty("functionName") String functionName,
                       @JsonProperty("tests") ArrayList<TestData> tests) {
        this.packageId = packageId;
        this.jsScript = jsScript;
        this.functionName = functionName;
        this.tests = tests;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getJsScript() {
        return jsScript;
    }

    public String getFunctionName() {
        return functionName;
    }

    public ArrayList<TestData> getTests() {
        return tests;
    }
}
