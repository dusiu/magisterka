package pl.edu.uj.dusinski;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.Direction;

@Component
@EnableScheduling
public class JmsPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsPublisher.class);

    @Autowired
    JmsTemplate jmsTemplate;

    public void pusblishAirportDetails(AirportDetails airportDetails) {
        LOGGER.info("Publishing new airport details {}", airportDetails.getId());
        jmsTemplate.convertAndSend("airportDetailsQueue", airportDetails);
    }

    public void publishDirection(Direction direction) {
        LOGGER.info("Publishing new direction {}", direction.getId());
        jmsTemplate.convertAndSend("directionQueue", direction);

    }

}
