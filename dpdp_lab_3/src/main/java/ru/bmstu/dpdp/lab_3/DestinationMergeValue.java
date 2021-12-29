package ru.bmstu.dpdp.lab_3;

import org.apache.spark.api.java.function.Function2;

public class DestinationMergeValue implements Function2<DestinationInfo, String, DestinationInfo> {
    @Override
    public DestinationInfo call(DestinationInfo destinationInfo1, String delay) throws Exception {
        return destinationInfo1.appendDestination(DestinationInfo.getDestinationInfo(delay));
    }
}
