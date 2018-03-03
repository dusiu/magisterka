package pl.edu.uj.dusinski.services;

import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.util.function.Function;

@Service
public class FlightsUrlGeneratorService implements Function<FlightDetails, String> {

    private static final String WIZZAIR_FLIGHTS_URL = "https://wizzair.com/en-gb#/booking/select-flight/%s/%s/%s";

    @Override
    public String apply(FlightDetails flightDetails) {
        if (Airline.WIZZAIR.equals(flightDetails.getDirection().getAirline())) {
            return String.format(WIZZAIR_FLIGHTS_URL, flightDetails.getDirection().getFromCode(),
                    flightDetails.getDirection().getToCode(), flightDetails.getFlyDate());
        } else {
            return "http://www.google.com";
        }
    }
}
