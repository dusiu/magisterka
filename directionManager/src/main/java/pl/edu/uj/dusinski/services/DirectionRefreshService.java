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

    public final String databaseManagerUrl;
    private final DirectionFinderService directionFinderService;
    private final RestTemplate restTemplate;

    public DirectionRefreshService(DirectionFinderService directionFinderService, RestTemplate restTemplate, @Value("${database.manager.url}") String databaseManagerUrl) {
        this.databaseManagerUrl = databaseManagerUrl;
        this.directionFinderService = directionFinderService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 10000)
    public void updateDirectionIfNeeded() {
        Log.info("Checking if we want to update directions");
        DirectionRefreshDetails latest = restTemplate
                .getForObject(databaseManagerUrl + "/directionManager/lastUpdatedTimeWizzair", DirectionRefreshDetails.class);

        boolean b = latest != null && LocalDateTime.now().minusWeeks(1).isAfter(latest.getUpdatingTime());
//        if (!latest.isPresent() || true) {
        if (b) {
            Log.info("Updating direction list");
            directionFinderService.updateDirections();
            restTemplate.getForObject(databaseManagerUrl + "/directionManager/updateNewDirections/WIZZAIR", String.class);
        }
    }
}
