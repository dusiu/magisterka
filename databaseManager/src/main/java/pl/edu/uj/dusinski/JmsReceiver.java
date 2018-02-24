package pl.edu.uj.dusinski;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pl.edu.uj.dusinski.dao.Direction;

@Component
public class JmsReceiver {

    @JmsListener(destination = "testQueue", containerFactory = "jmsListenerFactory")
    public void receiveMessage(Direction direction) {
        System.out.println("Received <" + direction + ">");
    }

}
