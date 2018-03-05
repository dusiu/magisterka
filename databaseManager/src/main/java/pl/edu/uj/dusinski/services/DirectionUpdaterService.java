package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.DirectionRefreshDetails;
import pl.edu.uj.dusinski.jpa.DirectionRefreshDetailsRepository;
import pl.edu.uj.dusinski.jpa.DirectionRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class DirectionUpdaterService {
    private static final Logger Log = LoggerFactory.getLogger(DirectionUpdaterService.class);

    private final Set<Direction> directions = new HashSet<>();
    private final DirectionRepository directionRepository;
    private final DirectionRefreshDetailsRepository refreshDetailsRepository;

    @Autowired
    public DirectionUpdaterService(DirectionRepository directionRepository, DirectionRefreshDetailsRepository refreshDetailsRepository) {
        this.directionRepository = directionRepository;
        this.refreshDetailsRepository = refreshDetailsRepository;
    }

    public void updateDirectionsInDatabase(Airline airline) {
        if (directions.isEmpty()) {
            Log.info("There are no directions to update");
            refreshDetailsRepository.save(new DirectionRefreshDetails(LocalDateTime.now(), directions.size(), airline));
            return;
        }
        directionRepository.saveAll(directions);
        Log.info("There are {} new direction saved into database", directions.size());
        directions.clear();
    }

    public void saveNewDirections(Direction direction) {
        if (directionRepository.findById(direction.getId()).isPresent()) {
            Log.info("Direction {} already exist in database", direction.getId());
        } else {
            Log.info("Adding new direction {} to update", direction.getId());
            directions.add(direction);
        }
    }

}
