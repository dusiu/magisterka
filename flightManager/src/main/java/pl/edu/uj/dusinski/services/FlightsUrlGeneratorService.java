package pl.edu.uj.dusinski.services;

import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.util.Map;
import java.util.function.Function;

import static pl.edu.uj.dusinski.dao.Airline.*;

@Service
public class FlightsUrlGeneratorService implements Function<FlightDetails, String> {

    private static final String WIZZAIR_FLIGHTS_URL = "https://wizzair.com/en-gb#/booking/select-flight/%s/%s/%s";
    private static final String RYANAIR_FLIGHTS_URL = "https://www.ryanair.com/pl/pl/booking/home/%s/%s/%s";

    private final Map<Airline, Function<FlightDetails, String>> airlineUrlMapping = Map.of(
            WIZZAIR, flightDetails -> String.format(WIZZAIR_FLIGHTS_URL, flightDetails.getDirection().getFromCode(),
                    flightDetails.getDirection().getToCode(), flightDetails.getFlyDate()),
            RYANAIR, flightDetails -> String.format(RYANAIR_FLIGHTS_URL, flightDetails.getDirection().getFromCode(),
                    flightDetails.getDirection().getToCode(), flightDetails.getFlyDate()),
            UNKNOWN, flightDetails -> "");

    @Override
    public String apply(FlightDetails flightDetails) {
        return airlineUrlMapping.getOrDefault(flightDetails.getDirection().getAirline(),
                airlineUrlMapping.get(UNKNOWN)).apply(flightDetails);
    }

}
