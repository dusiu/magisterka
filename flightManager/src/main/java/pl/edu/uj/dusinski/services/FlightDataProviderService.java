package pl.edu.uj.dusinski.services;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.FlightDetails;
import pl.edu.uj.dusinski.dao.FlightDetailsRequest;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class FlightDataProviderService {
    private static final Logger Log = LoggerFactory.getLogger(FlightDataProviderService.class);
    private static final int HOUR_IN_MS = 60 * 60 * 1000;

    private final RestTemplate restTemplate;
    private final Set<AirportDetails> directionFromWhereFlyTo = new HashSet<>();
    private final Map<String, AirportDetails> idAirportsMap = new HashMap<>();
    private final String databaseManagerUrl;
    private final String flyFromUrl = "/flights/flyFrom";
    private final String flyGetDirectionsUrl = "/flights/getDirections/";
    private final String airportsUrl = "/flights/getAllAirports";
    private final String flightsDetailsUrl = "/flights/flightDetails/";
    private final Gson gson = new Gson();
    private final Type flightDetailsType = new TypeToken<Set<FlightDetails>>() {
    }.getType();

    @Autowired
    public FlightDataProviderService(RestTemplate restTemplate,
                                     @Value("${database.manager.url}") String databaseManagerUrl) {
        this.restTemplate = restTemplate;
        this.databaseManagerUrl = databaseManagerUrl;
    }

    @Scheduled(fixedDelay = HOUR_IN_MS)
    public void updateDirectionsFromFlyToAndAirports() throws InterruptedException {
        try {
            AirportDetails[] fromDirection = gson.fromJson(restTemplate.getForObject(databaseManagerUrl + flyFromUrl, String.class), AirportDetails[].class);
            if (fromDirection != null && fromDirection.length > 0) {
                directionFromWhereFlyTo.clear();
                directionFromWhereFlyTo.addAll(Arrays.asList(fromDirection));
                Log.info("Updated {} directions from which can fly", fromDirection.length);
            }
            AirportDetails[] allAirports = gson.fromJson(restTemplate.getForObject(databaseManagerUrl + airportsUrl, String.class), AirportDetails[].class);
            if (allAirports != null && allAirports.length > 0) {
                idAirportsMap.clear();
                idAirportsMap.putAll(Arrays.stream(allAirports)
                        .collect(Collectors.toMap(AirportDetails::getId, v -> v)));
            }
        } catch (Exception e) {
            Log.error("Error during getting fly from list, probably database manager is not running, trying again in 10s", e);
            Thread.sleep(10_000);
            updateDirectionsFromFlyToAndAirports();
        }
    }

    public Set<AirportDetails> getAirportsForDirection(String direction) {
        try {
            AirportDetails[] flyToDirection = gson.fromJson(restTemplate.getForObject(databaseManagerUrl + flyGetDirectionsUrl + direction, String.class), AirportDetails[].class);
            if (flyToDirection != null && flyToDirection.length > 0) {
                return new HashSet<>(Arrays.asList(flyToDirection));
            }
            return Collections.emptySet();
        } catch (Exception e) {
            Log.error("Error during getting fly to direction list, probably database manager is not running", e);
            return Collections.emptySet();
        }
    }

    public AirportDetails getAirportDetails(String id) {
        return idAirportsMap.get(id);
    }

    public Set<AirportDetails> getDirectionFromWhereFlyTo() {
        return directionFromWhereFlyTo;
    }

    public Set<FlightDetails> getFlightDetailsForRequest(FlightDetailsRequest flightDetailsRequest) {
        try {
            return gson.fromJson(restTemplate
                            .getForObject(databaseManagerUrl + flightsDetailsUrl + flightDetailsRequest.getFromCode() + "/" + flightDetailsRequest.getToCode(), String.class),
                    flightDetailsType);
        } catch (Exception e) {
            Log.error("Error during getting airport details, probably database manager is not running", e);
        }
        return Collections.emptySet();
    }

}
