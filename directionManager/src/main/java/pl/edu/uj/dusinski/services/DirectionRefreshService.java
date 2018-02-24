package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.DirectionRefreshDetails;
import pl.edu.uj.dusinski.jpa.DirectionRefreshDetailsRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@EnableScheduling
public class DirectionRefreshService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectionRefreshService.class);

    private final DirectionFinderService directionFinderService;
    private final DirectionRefreshDetailsRepository directionRefreshDetailsRepository;

    public DirectionRefreshService(DirectionFinderService directionFinderService, DirectionRefreshDetailsRepository directionRefreshDetailsRepository) {
        this.directionFinderService = directionFinderService;
        this.directionRefreshDetailsRepository = directionRefreshDetailsRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void updateDirectionIfNeeded() {
        LOGGER.info("Checking if we want to update directions");
        Optional<DirectionRefreshDetails> latest = directionRefreshDetailsRepository.findById(directionRefreshDetailsRepository.count() - 1);
        boolean b = latest.isPresent() && latest.get().getUpdatingTime().minusWeeks(1).isAfter(LocalDateTime.now());
//        if (!latest.isPresent() || true) {
        if (!latest.isPresent() || b) {
            LOGGER.info("Updating direction list");
            directionFinderService.updateDirections();
        }
    }
}
