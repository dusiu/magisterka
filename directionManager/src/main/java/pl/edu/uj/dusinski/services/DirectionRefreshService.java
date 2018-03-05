package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
        checkWizzairDirections();
        checkRyanairDirections();
    }

    private void checkRyanairDirections() {
        DirectionRefreshDetails latest = restTemplate
                .getForObject(databaseManagerUrl + "/directionManager/lastUpdatedTimeRyanair", DirectionRefreshDetails.class);

        if (latest != null && LocalDateTime.now().minusWeeks(1).isAfter(latest.getUpdatingTime())) {
            if (ryanairDirectionFinderService.updateDirections()) {
                restTemplate.getForObject(databaseManagerUrl + "/directionManager/updateNewDirections/RYANAIR", String.class);
            }
        }
    }

    private void checkWizzairDirections() {
        DirectionRefreshDetails latest = restTemplate
                .getForObject(databaseManagerUrl + "/directionManager/lastUpdatedTimeWizzair", DirectionRefreshDetails.class);

        if (latest != null && LocalDateTime.now().minusWeeks(1).isAfter(latest.getUpdatingTime())) {
            if (wizzairDirectionFinderService.updateDirections()) {
                restTemplate.getForObject(databaseManagerUrl + "/directionManager/updateNewDirections/WIZZAIR", String.class);
            }
        }
    }
}
