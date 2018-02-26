package pl.edu.uj.dusinski;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.FlightDetails;
import pl.edu.uj.dusinski.services.AirportDetailsUpdaterService;
import pl.edu.uj.dusinski.services.DirectionUpdaterService;
import pl.edu.uj.dusinski.services.FlightDetailsUpdaterService;

@Component
public class JmsReceiver {
    private static final Logger Log = LoggerFactory.getLogger(JmsReceiver.class);

    private final DirectionUpdaterService directionUpdaterService;
    private final AirportDetailsUpdaterService airportDetailsUpdaterService;
    private final FlightDetailsUpdaterService flightDetailsUpdaterService;

    @Autowired
    public JmsReceiver(DirectionUpdaterService directionUpdaterService,
                       AirportDetailsUpdaterService airportDetailsUpdaterService,
                       FlightDetailsUpdaterService flightDetailsUpdaterService) {
        this.directionUpdaterService = directionUpdaterService;
        this.airportDetailsUpdaterService = airportDetailsUpdaterService;
        this.flightDetailsUpdaterService = flightDetailsUpdaterService;
    }

    @JmsListener(destination = "directionQueue", containerFactory = "jmsListenerFactory")
    public void receiveDirectionMessage(Direction direction) {
        Log.info("Received new direction {}", direction);
        directionUpdaterService.saveNewDirections(direction);
    }

    @JmsListener(destination = "airportDetailsQueue", containerFactory = "jmsListenerFactory")
    public void receiveAirportDetailsMessage(AirportDetails airportDetails) {
        Log.info("Received new airport details {}", airportDetails);
        airportDetailsUpdaterService.updateAirportDetails(airportDetails);
    }

    @JmsListener(destination = "flightDetailsQueue", containerFactory = "jmsListenerFactory")
    public void receiveFlightDetailsMessage(FlightDetails flightDetails) {
        Log.info("Received new flight details {}", flightDetails);
        flightDetailsUpdaterService.updateFlightDetails(flightDetails);
    }

}
