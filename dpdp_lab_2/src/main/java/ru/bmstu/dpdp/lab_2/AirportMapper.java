package ru.bmstu.dpdp.lab_2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AirportMapper extends Mapper<LongWritable, Text, AirportWritableComparable, Text> {


    public static final int AIRPORT_ID = 0;
    public static final int AIRPORT_DESCRIPTION = 1;
    public static final int AIRPORT_DATA_FLAG = 0;
    public static final String SEPARATOR = "\",\"";

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException,
            InterruptedException {
        String[] row = value.toString().split(SEPARATOR);
        if (key.get() > 0) {
            int airportId = Integer.parseInt(removeQuote(row[AIRPORT_ID], 0));
            String airportName = row[AIRPORT_DESCRIPTION];
            context.write(new AirportWritableComparable(airportId, AIRPORT_DATA_FLAG),
                    new Text(removeQuote(airportName, airportName.length() - 1)));
        }
    }

    private String removeQuote(String str, int quotePos) {
        StringBuilder result = new StringBuilder(str);
        result.deleteCharAt(quotePos);
        return result.toString();
    }

}