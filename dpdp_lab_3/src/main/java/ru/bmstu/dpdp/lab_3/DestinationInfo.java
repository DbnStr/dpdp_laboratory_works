package ru.bmstu.dpdp.lab_3;

import scala.Tuple2;

import java.io.Serializable;
import java.util.Map;

public class DestinationInfo implements Serializable {

    private Float maxDelay;
    private int flightsAmount;
    private int delayAmount;
    private int cancelledAmount;

    public DestinationInfo(Float maxDelay, int flightsAmount, int delayAmount, int cancelledAmount) {
        this.maxDelay = maxDelay;
        this.flightsAmount = flightsAmount;
        this.delayAmount = delayAmount;
        this.cancelledAmount = cancelledAmount;
    }

    public DestinationInfo appendDestination(DestinationInfo destinationInfo) {
        this.cancelledAmount += destinationInfo.cancelledAmount;
        this.maxDelay = this.maxDelay > destinationInfo.maxDelay ? this.maxDelay : destinationInfo.maxDelay;
        this.delayAmount += destinationInfo.delayAmount;
        this.flightsAmount += destinationInfo.flightsAmount;
        return this;
    }

    public static boolean isCancelled(String delay) {
        return delay.isEmpty();
    }

    public static DestinationInfo getCancelledFlight() {
        return new DestinationInfo(0.f, 1, 0, 1);
    }

    public static DestinationInfo getDestinationInfo(String delay) {
        if (DestinationInfo.isCancelled(delay)) {
            return DestinationInfo.getCancelledFlight();
        }
        if (Float.parseFloat(delay) == 0.f) {
            return new DestinationInfo(0.f, 1, 0, 0);
        } else {
            return new DestinationInfo(Float.parseFloat(delay), 1, 1, 0);
        }
    }

    public static String getResult(Tuple2<Tuple2<String, String>, DestinationInfo> flightsDest, Map<String, String> airports) {
        String originalAirportId = flightsDest._1._1;
        String originalAirportName = airports.get(originalAirportId);
        String destAirportId = flightsDest._1._2;
        String destAirportName = airports.get(destAirportId);
        DestinationInfo destinationInfo = flightsDest._2();
        return originalAirportName + "  -->  " + destAirportName+"\n" +
                "    MaxDelay : " + destinationInfo.maxDelay + "\n" +
                "    DelayAmount : " + ((float) destinationInfo.delayAmount / destinationInfo.flightsAmount) * 100 + "%" + "\n" +
                "    CancelledAmount : " + ((float) destinationInfo.cancelledAmount / destinationInfo.flightsAmount) * 100 + "%" + "\n";
    }
}
