package ru.bmstu.dpdp.lab_3;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Map;

public class AirportApp {

    public static final int AIRPORT_ID = 0;
    public static final int AIRPORT_NAME = 1;

    public static final int ORIGINAL_AIRPORT_ID = 11;
    public static final int DEST_AIRPORT_ID = 14;
    public static final int FLIGHT_DELAY = 18;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Input format : <flights table path> <airports table path> <output path>");
            System.exit(-1);
        }
        SparkConf conf = new SparkConf().setAppName("lab_3");
        JavaSparkContext sc = new JavaSparkContext(conf);

        Map<String, String> airports = sc
                .hadoopFile(args[1], TextInputFormat.class, LongWritable.class, Text.class)
                .filter(tableRow -> tableRow._1.get() != 0)
                .mapToPair(tableRow -> Parser.parseAirportRow(tableRow._2.toString()))
                .collectAsMap();

        final Broadcast<Map<String, String>> airportsBroadcasted = sc.broadcast(airports);

        JavaPairRDD<Tuple2<String, String>, DestinationInfo> destinations = sc
                .hadoopFile(args[0], TextInputFormat.class, LongWritable.class, Text.class)
                .filter(tableRow -> tableRow._1.get() != 0)
                .mapToPair(tableRow -> Parser.parseFlightsRow(tableRow._2.toString()))
                .combineByKey(new DestinationCreateCombiner(), new DestinationMergeValue(), new DestinationMergeCombiners());

        JavaRDD<String> result = destinations.map(
                pair -> DestinationInfo.getResult(pair, airportsBroadcasted.value())
        );

        result.saveAsTextFile(args[2]);
    }
}
