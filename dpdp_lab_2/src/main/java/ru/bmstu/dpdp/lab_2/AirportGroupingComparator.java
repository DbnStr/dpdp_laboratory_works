package ru.bmstu.dpdp.lab_2;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class AirportGroupingComparator extends WritableComparator {

    public AirportGroupingComparator() {
        super(AirportWritableComparable.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        AirportWritableComparable key1 = (AirportWritableComparable) a;
        AirportWritableComparable key2 = (AirportWritableComparable) b;
        return Integer.compare(key1.getAirportId(), key2.getAirportId());
    }
}
