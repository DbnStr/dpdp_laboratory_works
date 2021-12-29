package ru.bmstu.dpdp.lab_4.messages;

import java.util.ArrayList;

public class ExecuteMessage {
    private final String packageId, jsScript, functionName, testName, expectedResult;
    private final ArrayList<Integer> params;

    public ExecuteMessage(String packageId,
                          String jsScript,
                          String functionName,
                          String testName,
                          String expectedResult,
                          ArrayList<Integer> params) {
        this.packageId = packageId;
        this.jsScript = jsScript;
        this.functionName = functionName;
        this.testName = testName;
        this.expectedResult = expectedResult;
        this.params = params;
    }

    public static ExecuteMessage getExecuteMessage(PackageData packageData, TestData testData) {
        return new ExecuteMessage (
                packageData.getPackageId(),
                packageData.getJsScript(),
                packageData.getFunctionName(),
                testData.getTestName(),
                testData.getExpectedResult(),
                testData.getParams()
        );
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

    public String getTestName() {
        return testName;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public ArrayList<Integer> getParams() {
        return params;
    }
}
