package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.FlightDetails;
import pl.edu.uj.dusinski.jpa.FlightDetailsRepository;

@Service
public class FlightDetailsUpdaterService {
    private static final Logger Log = LoggerFactory.getLogger(FlightDetailsUpdaterService.class);

    private final FlightDetailsRepository flightDetailsRepository;

    @Autowired
    public FlightDetailsUpdaterService(FlightDetailsRepository flightDetailsRepository) {
        this.flightDetailsRepository = flightDetailsRepository;
    }

    public void updateFlightDetails(FlightDetails flightDetails) {
        Log.info("Adding new airport details with id:{}", flightDetails.getId());
        flightDetailsRepository.save(flightDetails);
    }
}
