package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.DirectionRefreshDetails;
import pl.edu.uj.dusinski.jpa.DirectionRefreshDetailsRepository;
import pl.edu.uj.dusinski.jpa.DirectionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pl.edu.uj.dusinski.dao.Airline.WIZZAIR;

@Service
public class DirectionUpdaterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectionUpdaterService.class);

    private final List<Direction> directions = new ArrayList<>();
    private final DirectionRepository directionRepository;
    private final DirectionRefreshDetailsRepository refreshDetailsRepository;

    @Autowired
    public DirectionUpdaterService(DirectionRepository directionRepository, DirectionRefreshDetailsRepository refreshDetailsRepository) {
        this.directionRepository = directionRepository;
        this.refreshDetailsRepository = refreshDetailsRepository;
    }

    public void updateDirectionsInDatabase() {
        if (directions.isEmpty()) {
            LOGGER.info("There are no directions to update");
            return;
        }
        directionRepository.saveAll(directions);
        directions.clear();
        LOGGER.info("There are {} new direction in database", directions.size());
        refreshDetailsRepository.save(new DirectionRefreshDetails(refreshDetailsRepository.count(), LocalDateTime.now(), directions.size(), WIZZAIR));
    }

    public void saveNewDirections(Direction direction) {
        if (directionRepository.findById(direction.getId()).isPresent()) {
            LOGGER.info("Direction {} already exist in database", direction.getId());
        } else {
            LOGGER.info("Adding new direction {} to database", direction.getId());
            directions.add(direction);
        }
    }

}
