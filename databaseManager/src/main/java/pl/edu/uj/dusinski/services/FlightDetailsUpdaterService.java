package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.FlightDetails;
import pl.edu.uj.dusinski.jpa.FlightDetailsRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FlightDetailsUpdaterService {
    private static final Logger Log = LoggerFactory.getLogger(FlightDetailsUpdaterService.class);

    private final FlightDetailsRepository flightDetailsRepository;
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Autowired
    public FlightDetailsUpdaterService(FlightDetailsRepository flightDetailsRepository) {
        this.flightDetailsRepository = flightDetailsRepository;
    }

    public void updateFlightDetails(FlightDetails flightDetails) {
        if (atomicInteger.incrementAndGet() == 500) {
            Log.info("Added 500 new flight details");
        }
        flightDetailsRepository.save(flightDetails);
    }
}
