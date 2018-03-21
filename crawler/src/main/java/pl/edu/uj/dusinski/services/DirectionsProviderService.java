package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static pl.edu.uj.dusinski.dao.Airline.RYANAIR;
import static pl.edu.uj.dusinski.dao.Airline.WIZZAIR;

@Service
@EnableScheduling
public class DirectionsProviderService {
    private static final Logger Log = LoggerFactory.getLogger(DirectionsProviderService.class);

    private final RestTemplate restTemplate;
    private final String databaseManagerUrl;
    private final String directionUrl = "/directions/allDirections/";
    private final Map<Airline, List<Direction>> airlineDirections = new HashMap<>();
    private final long oneDayInMs = 24 * 60 * 60 * 1000;
    private final Map<String, Direction> ryanairDirectionMap = new HashMap<>();

    @Autowired
    public DirectionsProviderService(RestTemplate restTemplate,
                                     @Value("${database.manager.url}") String databaseManagerUrl) {
        this.restTemplate = restTemplate;
        this.databaseManagerUrl = databaseManagerUrl;
    }

    public List<Direction> getDirectionsFor(Airline airline) {
        if (RYANAIR.equals(airline)) {
            return new ArrayList<>(airlineDirections.get(airline).stream()
                    .collect(Collectors.toMap(Direction::getFromCode, v -> v, (v1, v2) -> v1))
                    .values());
        }
        return airlineDirections.getOrDefault(airline, Collections.emptyList());
    }

    public Direction getDirectionForRyanair(String codeFrom, String codeTo) {
        return ryanairDirectionMap.get(codeFrom + codeTo);
    }

    @Scheduled(fixedDelay = oneDayInMs, initialDelay = oneDayInMs)
    public void updateDirectionsOncePerDay() {
        updateDirections();
    }

    @PostConstruct
    public void updateDirections() {
        updateDirections(WIZZAIR);
        updateDirections(RYANAIR);
    }


    private void updateDirections(Airline airline) {
        Log.info("Updating directions list for {}", airline);
        try {
            List<Direction> directions = Arrays.asList(restTemplate.getForObject(getUrlForAirline(airline), Direction[].class));
            airlineDirections.put(airline, directions);
            if (RYANAIR.equals(airline)) {
                ryanairDirectionMap.putAll(directions.stream()
                        .collect(Collectors.toMap(k -> k.getFromCode() + k.getToCode(), v -> v)));
            }
            Log.info("There are {} different directions for {}", directions.size(), airline);
        } catch (Exception e) {
            Log.error("Cannot get wizziar directions, turn on database manager!");
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e1) {
            }
            updateDirections();
        }
    }

    private String getUrlForAirline(Airline airline) {
        return databaseManagerUrl + directionUrl + airline.name();
    }

}
