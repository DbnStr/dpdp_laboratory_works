package ru.bmstu.dpdp.lab_2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlightMapper extends Mapper<LongWritable, Text, AirportWritableComparable, Text> {

    public static final int DEST_AIRPORT_ID = 14;
    public static final int FLIGHT_DELAY = 18;
    public static final int CANCELLED_STATUS = 19;
    
    public static final float CANCELLED = 1.0f;
    public static final int FLIGHT_DATA_FLAG = 1;
    public static final String SEPARATOR = ",";

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException,
            InterruptedException {
        String[] row = value.toString().split(SEPARATOR);

        if (key.get() > 0) {
            String destAirportId = row[DEST_AIRPORT_ID];
            String flightDelay = row[FLIGHT_DELAY];
            boolean cancelled = Float.parseFloat(row[CANCELLED_STATUS]) == CANCELLED;
            if (!cancelled) {
                if (!flightDelay.isEmpty()) { //ПОЧЕМУ-ТО В ДАННЫХ ПРИ НЕОТМЕНЁННОМ РЕЙСЕ ПУСТОЕ ВРНМЯ ЗАДЕРЖКИ!!
                    float delay = Float.parseFloat(flightDelay);
                    if (delay > 0.0f)
                        context.write(new AirportWritableComparable(Integer.parseInt(destAirportId), FLIGHT_DATA_FLAG), new Text(flightDelay));
                }
            }
        }
    }
}