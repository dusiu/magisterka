package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@EnableScheduling
public class DirectionsProviderService {
    private static final Logger Log = LoggerFactory.getLogger(WebDriverMangerService.class);

    private final RestTemplate restTemplate;
    private final String databaseManagerUrl;
    private final String directionUrl = "/directions/allDirections/";
    private final Map<Airline, List<Direction>> airlineDirections = new HashMap<>();
    private final long oneDayInMs = 24 * 60 * 60 * 1000;

    @Autowired
    public DirectionsProviderService(RestTemplate restTemplate, @Value("${database.manager.url}") String databaseManagerUrl) {
        this.restTemplate = restTemplate;
        this.databaseManagerUrl = databaseManagerUrl;
    }

    public List<Direction> getDirectionsFor(Airline airline) {
        return airlineDirections.getOrDefault(airline, Collections.emptyList());
    }

    @Scheduled(fixedDelay = oneDayInMs, initialDelay = oneDayInMs)
    public void updateDirectionsOncePerDay() {
        updateDirections();
    }

    @PostConstruct
    public void updateDirections() {
        Log.info("Updating directions list for Wizzair");
        List<Direction> wizzairDirections = Arrays.asList(restTemplate.getForObject(getUrlForAirline(Airline.WIZZAIR), Direction[].class));
        airlineDirections.put(Airline.WIZZAIR, wizzairDirections);
        Log.info("There are {} different direztions for Wizzair", wizzairDirections.size());
    }

    private String getUrlForAirline(Airline airline) {
        return databaseManagerUrl + directionUrl + airline.name();
    }

}
