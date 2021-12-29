package ru.bmstu.dpdp.lab_3;

import org.apache.spark.api.java.function.Function2;

public class DestinationMergeCombiners implements Function2<DestinationInfo, DestinationInfo, DestinationInfo> {
    @Override
    public DestinationInfo call(DestinationInfo destinationInfo1, DestinationInfo destinationInfo2) throws Exception {
        return destinationInfo1.appendDestination(destinationInfo2);
    }
}
