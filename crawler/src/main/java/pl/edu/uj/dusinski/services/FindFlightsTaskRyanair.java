package pl.edu.uj.dusinski.services;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.FlightDetailsData;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import static pl.edu.uj.dusinski.dao.Airline.RYANAIR;
import static pl.edu.uj.dusinski.services.FlightsDetailsFinderService.logTaskFinished;

public class FindFlightsTaskRyanair implements Callable<Void> {
    private static final Logger Log = LoggerFactory.getLogger(FindFlightsTaskRyanair.class);

    private final JmsPublisher jmsPublisher;
    private final Direction direction;
    private final int daysToCheck;
    private final RestTemplate restTemplate;
    private final String RYANAIR_FLIGHTS_URL = "https://api.ryanair.com/farefinder/3/oneWayFares?&departureAirportIataCode=%s&outboundDepartureDateFrom=%s&outboundDepartureDateTo=%s";
    private final Gson gson = new Gson();
    private final DirectionsProviderService directionsProviderService;

    public FindFlightsTaskRyanair(RestTemplate restTemplate, JmsPublisher jmsPublisher, Direction direction, int daysToCheck, DirectionsProviderService directionsProviderService) {
        this.restTemplate = restTemplate;
        this.jmsPublisher = jmsPublisher;
        this.direction = direction;
        this.daysToCheck = daysToCheck;
        this.directionsProviderService = directionsProviderService;
    }

    @Override
    public Void call() throws Exception {
        int weeksToCheck = (int) Math.ceil(daysToCheck / 7.0);
        int publishedFlights = 0;
        for (int i = 0; i < weeksToCheck; i++) {
            FlightDetailsData flightDetailsData = gson.fromJson(restTemplate.getForObject(prepareUrl(direction, i), String.class), FlightDetailsData.class);
            flightDetailsData.getFares().stream()
                    .map(this::mapToFlightDetails)
                    .forEach(jmsPublisher::publishNewFlightDetails);
            publishedFlights += flightDetailsData.getFares().size();
        }
        Log.info("Published {} flight details for {}", publishedFlights, direction.getFromCode());
        logTaskFinished();
        return null;
    }

    private FlightDetails mapToFlightDetails(FlightDetailsData.Fare v) {
        String fromCode = v.getOutbound().getDepartureAirport().getIataCode();
        String toCode = v.getOutbound().getArrivalAirport().getIataCode();
        return new FlightDetails(fromCode + toCode + RYANAIR + v.getOutbound().getDepartureDate().toString(),
                v.getOutbound().getDepartureDate(), directionsProviderService.getFirectionForRyanair(fromCode, toCode),
                v.getSummary().getPrice().getValue(), v.getSummary().getPrice().getCurrencyCode());
    }

    private String prepareUrl(Direction direction, int i) {
        return String.format(RYANAIR_FLIGHTS_URL, direction.getFromCode(),
                LocalDate.now().plusDays(1).plusWeeks(i),
                LocalDate.now().plusDays(1).plusMonths(1).plusWeeks(i));
    }
}
