package pl.edu.uj.dusinski;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.services.AirportDetailsUpdaterService;
import pl.edu.uj.dusinski.services.DirectionUpdaterService;

@Component
public class JmsReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsReceiver.class);

    private final DirectionUpdaterService directionUpdaterService;
    private final AirportDetailsUpdaterService airportDetailsUpdaterService;

    @Autowired
    public JmsReceiver(DirectionUpdaterService directionUpdaterService,
                       AirportDetailsUpdaterService airportDetailsUpdaterService) {
        this.directionUpdaterService = directionUpdaterService;
        this.airportDetailsUpdaterService = airportDetailsUpdaterService;
    }

    @JmsListener(destination = "directionQueue", containerFactory = "jmsListenerFactory")
    public void receiveDirectionMessage(Direction direction) {
        LOGGER.info("Received new direction {}", direction);
        directionUpdaterService.saveNewDirections(direction);
    }

    @JmsListener(destination = "airportDetailsQueue", containerFactory = "jmsListenerFactory")
    public void receiveAirportDetailsMessage(AirportDetails airportDetails) {
        LOGGER.info("Received new airport details {}", airportDetails);
        airportDetailsUpdaterService.updateAirportDetails(airportDetails);
    }

}
