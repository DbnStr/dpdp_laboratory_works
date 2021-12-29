package ru.bmstu.dpdp.lab_1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class WordReducer extends Reducer<Text, IntWritable, Text, LongWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws
            IOException, InterruptedException {
        int resultValue = 0;
        Iterator<IntWritable> valuesIt = values.iterator();
        while (valuesIt.hasNext()) {
            resultValue += valuesIt.next().get();
        }
        context.write(key, new LongWritable(resultValue));
    }
}