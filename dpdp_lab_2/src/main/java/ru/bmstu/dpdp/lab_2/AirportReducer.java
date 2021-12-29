package ru.bmstu.dpdp.lab_2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class AirportReducer extends Reducer<AirportWritableComparable, Text, Text, Text> {
    @Override
    protected void reduce(AirportWritableComparable key, Iterable<Text> values, Context context) throws
            IOException, InterruptedException {
        float maxDelay = Float.MIN_VALUE;
        float minDelay = Float.MAX_VALUE;
        float sumDelay = 0.0f;
        int countOfFlight = 0;
        Iterator<Text> iter = values.iterator();
        Text airportName = new Text(iter.next());
        while (iter.hasNext()) {
            String delay = iter.next().toString();
            float delayValue = Float.parseFloat(delay);
            if (delayValue > maxDelay) {
                maxDelay = delayValue;
            }
            if (delayValue < minDelay) {
                minDelay = delayValue;
            }
            sumDelay += delayValue;
            countOfFlight++;
        }

        if (countOfFlight != 0)  {
            context.write(new Text("AirportName : " + airportName),
                    new Text("\n    min delay : " +  minDelay +
                            "\n" + "    max delay : " + maxDelay +
                            "\n" + "    average delay : " + sumDelay / countOfFlight));
        }
    }
}