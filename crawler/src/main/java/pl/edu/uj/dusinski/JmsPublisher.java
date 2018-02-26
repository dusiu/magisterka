package pl.edu.uj.dusinski;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.FlightDetails;

@Service
public class JmsPublisher {
    private static final Logger Log = LoggerFactory.getLogger(JmsPublisher.class);

    private final JmsTemplate jmsTemplate;

    public JmsPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void publishNewFlightDetails(FlightDetails flightDetails) {
        Log.info("Publishing new flight details, {}", flightDetails.getId());
        jmsTemplate.convertAndSend("flightDetailsQueue", flightDetails);
    }
}
