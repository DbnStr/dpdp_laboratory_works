package ru.bmstu.dpdp.lab_2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class AirportPartitioner extends Partitioner<AirportWritableComparable, Text> {

    @Override
    public int getPartition(AirportWritableComparable airportWritableComparable, Text text, int numReduceTasks) {
        return airportWritableComparable.getAirportId() % numReduceTasks;
    }
}
