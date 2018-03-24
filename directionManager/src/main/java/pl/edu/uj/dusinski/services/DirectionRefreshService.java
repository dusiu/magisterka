package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.DirectionRefreshDetails;

import java.time.LocalDateTime;

@Service
@EnableScheduling
public class DirectionRefreshService {
    private static final Logger Log = LoggerFactory.getLogger(DirectionRefreshService.class);

    private final String databaseManagerUrl;
    private final WizzairDirectionFinderService wizzairDirectionFinderService;
    private final RyanairDirectionFinderService ryanairDirectionFinderService;
    private final RestTemplate restTemplate;

    public DirectionRefreshService(WizzairDirectionFinderService wizzairDirectionFinderService,
                                   RestTemplate restTemplate,
                                   @Value("${database.manager.url}") String databaseManagerUrl,
                                   RyanairDirectionFinderService ryanairDirectionFinderService) {
        this.databaseManagerUrl = databaseManagerUrl;
        this.wizzairDirectionFinderService = wizzairDirectionFinderService;
        this.restTemplate = restTemplate;
        this.ryanairDirectionFinderService = ryanairDirectionFinderService;
    }

    @Scheduled(fixedDelay = 24 * 60 * 1000)
    public void updateDirectionIfNeeded() {
        Log.info("Checking if we want to update directions");
        checkDirections(Airline.RYANAIR);
        checkDirections(Airline.WIZZAIR);
    }

    private void checkDirections(Airline airline) {
        try {
            DirectionRefreshDetails latest = restTemplate
                    .getForObject(databaseManagerUrl + "/directionManager/lastUpdatedTime/" + airline.name(), DirectionRefreshDetails.class);
            if (latest != null && LocalDateTime.now().minusWeeks(1).isAfter(latest.getUpdatingTime())) {
                if (airline == Airline.WIZZAIR) {
                    if (wizzairDirectionFinderService.updateDirections()) {
                        sendUpdateFinishCall(airline);
                    }
                } else {
                    if (ryanairDirectionFinderService.updateDirections()) {
                        sendUpdateFinishCall(airline);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Error during checking directions probably direction manager is not running, retrying in 10s", e);
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e1) {
            }
            checkDirections(airline);
        }
    }

    private void sendUpdateFinishCall(Airline airline) {
        restTemplate.getForObject(databaseManagerUrl + "/directionManager/updateNewDirections/" + airline.name(), String.class);
    }
}
