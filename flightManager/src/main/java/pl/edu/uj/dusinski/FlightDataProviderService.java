package pl.edu.uj.dusinski;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@EnableScheduling
public class FlightDataProviderService {
    private static final Logger Log = LoggerFactory.getLogger(FlightDataProviderService.class);
    private static final int HOUR_IN_MS = 60 * 60 * 1000;

    private final RestTemplate restTemplate;
    private final Set<String> directionFromWhereFlyTo = new HashSet<>();
    private final String databaseManagerUrl;
    private final String flyFromUrl = "/flights/flyFrom";
    private final String flyGetDirections = "/flights/getDirections/";

    @Autowired
    public FlightDataProviderService(RestTemplate restTemplate,
                                     @Value("${database.manager.url}") String databaseManagerUrl) {
        this.restTemplate = restTemplate;
        this.databaseManagerUrl = databaseManagerUrl;
    }

    @Scheduled(fixedDelay = HOUR_IN_MS)
    public void updateDirectionsFromFlyTo() {
        try {
            String[] fromDirection = restTemplate.getForObject(databaseManagerUrl + flyFromUrl, String[].class);
            if (fromDirection.length > 0) {
                directionFromWhereFlyTo.clear();
                directionFromWhereFlyTo.addAll(Arrays.asList(fromDirection));
                Log.info("Updated {} directions from which can fly", fromDirection.length);
            }
        } catch (Exception e) {
            Log.error("Error during getting fly from list, probably database manager is not running", e);
        }
    }

    public Set<String> getFlyightsForDirection(String direction) {
        try {
            String[] flyToDirection = restTemplate.getForObject(databaseManagerUrl + flyGetDirections + direction, String[].class);
            if (flyToDirection.length > 0) {
                return new HashSet<>(Arrays.asList(flyToDirection));
            }
            return Collections.emptySet();
        } catch (Exception e) {
            Log.error("Error during getting fly to direction list, probably database manager is not running", e);
            return Collections.emptySet();
        }
    }

    public Set<String> getDirectionFromWhereFlyTo() {
        return directionFromWhereFlyTo;
    }
}
