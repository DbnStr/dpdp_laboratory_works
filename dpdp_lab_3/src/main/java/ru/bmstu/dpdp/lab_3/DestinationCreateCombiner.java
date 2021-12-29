package ru.bmstu.dpdp.lab_3;

import org.apache.spark.api.java.function.Function;

public class DestinationCreateCombiner implements Function<String, DestinationInfo> {

    @Override
    public DestinationInfo call(String delay) throws Exception {
        return DestinationInfo.getDestinationInfo(delay);
    }
}
