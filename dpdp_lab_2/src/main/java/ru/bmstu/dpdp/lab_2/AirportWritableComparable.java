package ru.bmstu.dpdp.lab_2;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AirportWritableComparable implements WritableComparable<AirportWritableComparable> {

    private int airportId;
    private int dataFlag;

    public AirportWritableComparable(int airportId, int dataFlag) {
        super();
        this.airportId = airportId;
        this.dataFlag = dataFlag;
    }

    public AirportWritableComparable() {

    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(airportId);
        out.writeInt(dataFlag);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        airportId = in.readInt();
        dataFlag = in.readInt();
    }

    public int getAirportId() {
        return airportId;
    }

    @Override
    public int compareTo(AirportWritableComparable o) {
        int airportIdComp = Integer.compare(this.airportId, o.airportId);
        int dataFlagComp = Integer.compare(this.dataFlag, o.dataFlag);
        return airportIdComp == 0 ? dataFlagComp : airportIdComp;
    }
}
