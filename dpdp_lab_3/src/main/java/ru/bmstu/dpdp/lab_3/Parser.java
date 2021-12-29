package ru.bmstu.dpdp.lab_3;

import scala.Tuple2;

public class Parser {

    public static final int AIRPORT_ID = 0;
    public static final int AIRPORT_NAME = 1;

    public static final int ORIGINAL_AIRPORT_ID = 11;
    public static final int DEST_AIRPORT_ID = 14;
    public static final int FLIGHT_DELAY = 18;

    public static Tuple2<String, String> parseAirportRow(String row) {
        String[] columns = row.split("\",\"");
        columns[0] = removeCharacter(columns[0], 0);
        String lastColumn = columns[columns.length - 1];
        columns[columns.length - 1] = removeCharacter(lastColumn, lastColumn.length() - 1);
        return new Tuple2<>(columns[AIRPORT_ID], columns[AIRPORT_NAME]);
    }

    public static Tuple2<Tuple2<String, String>, String> parseFlightsRow(String row) {
        String[] columns = row.split(",");
        return new Tuple2<>(new Tuple2<>(columns[ORIGINAL_AIRPORT_ID], columns[DEST_AIRPORT_ID]),
                columns[FLIGHT_DELAY]);
    }


    private static String removeCharacter(String str, int charPos) {
        StringBuilder result = new StringBuilder(str);
        result.deleteCharAt(charPos);
        return result.toString();
    }
}
