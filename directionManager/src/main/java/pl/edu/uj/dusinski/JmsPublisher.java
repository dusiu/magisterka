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
    private static final Logger Log = LoggerFactory.getLogger(JmsPublisher.class);

    private final JmsTemplate jmsTemplate;

    @Autowired
    public JmsPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void publishAirportDetails(AirportDetails airportDetails) {
        Log.info("Publishing new airport details {}", airportDetails.getId());
        jmsTemplate.convertAndSend("airportDetailsQueue", airportDetails);
    }

    public void publishDirection(Direction direction) {
        Log.info("Publishing new direction {}", direction.getId());
        jmsTemplate.convertAndSend("directionQueue", direction);

    }

}
