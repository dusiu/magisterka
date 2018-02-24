package pl.edu.uj.dusinski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;

import javax.annotation.PostConstruct;

@Component
@EnableScheduling
public class JmsPublisher {

    @Autowired
    JmsTemplate jmsTemplate;

    @Scheduled(fixedRate = 5000)
    public void pusblish() {
        jmsTemplate.convertAndSend("testQueue", new Direction("WROKAT","WRO","KAT", Airline.WIZZIAR));
    }

}
