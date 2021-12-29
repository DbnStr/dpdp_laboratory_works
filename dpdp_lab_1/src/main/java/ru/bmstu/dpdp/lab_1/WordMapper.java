package ru.bmstu.dpdp.lab_1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;

public class WordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException,
            InterruptedException {
        String line = value.toString();
        StringTokenizer symbols = new StringTokenizer(line, " ");

        Text resultKey = new Text();
        IntWritable resultValue = new IntWritable(1);

        while (symbols.hasMoreTokens()) {
            resultKey.set(symbols.nextToken().toLowerCase(Locale.ROOT));
            context.write(resultKey, resultValue);
        }
    }
}