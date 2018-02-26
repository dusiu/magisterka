package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.jpa.AirportDetailsRepository;

@Service
public class AirportDetailsUpdaterService {
    private static final Logger Log = LoggerFactory.getLogger(AirportDetailsUpdaterService.class);

    private final AirportDetailsRepository airportDetailsRepository;

    @Autowired
    public AirportDetailsUpdaterService(AirportDetailsRepository airportDetailsRepository) {
        this.airportDetailsRepository = airportDetailsRepository;
    }

    public void updateAirportDetails(AirportDetails airportDetails) {
        if (airportDetailsRepository.findById(airportDetails.getId()).isPresent()) {
            Log.info("Airport details already exist in database", airportDetails.getId());
        }else{
            Log.info("Adding new airport details with id:{}", airportDetails.getId());
            airportDetailsRepository.save(airportDetails);
        }
    }
}
