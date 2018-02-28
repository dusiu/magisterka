package pl.edu.uj.dusinski.services;

import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.util.function.Function;

@Service
public class FlightsUrlGeneratorService implements Function<FlightDetails, String> {

    @Override
    public String apply(FlightDetails flightDetails) {
        return "www.google.com";
    }
}
