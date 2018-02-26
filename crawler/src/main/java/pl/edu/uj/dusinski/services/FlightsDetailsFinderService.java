package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@EnableScheduling
public class FlightsDetailsFinderService {
    private static final Logger Log = LoggerFactory.getLogger(WebDriverMangerService.class);
    private static final String WIZZAIR_FLIGHTS_URL = "https://wizzair.com/en-gb#/booking/select-flight/%s/%s/%s";

    private final DirectionsProviderService directionsProviderService;
    private final WebDriverMangerService webDriverMangerService;
    private final ExecutorService executorService;
    private final JmsPublisher jmsPublisher;
    private final int daysToCheck;

    @Autowired
    public FlightsDetailsFinderService(DirectionsProviderService directionsProviderService,
                                       WebDriverMangerService webDriverMangerService,
                                       JmsPublisher jmsPublisher,
                                       @Value("${web.driver.instances}") int webDriverInstances,
                                       @Value("${days.to.check}") int daysToCheck) {
        this.directionsProviderService = directionsProviderService;
        this.webDriverMangerService = webDriverMangerService;
        this.jmsPublisher = jmsPublisher;
        this.executorService = Executors.newFixedThreadPool(webDriverInstances);
        this.daysToCheck = daysToCheck;
    }

    @Scheduled(fixedDelay = 10_000)
    public void findDirectionsDetails() {
        findAllWizzairFlights();
    }

    private void findAllWizzairFlights() {
        List<Direction> wizzairDirections = directionsProviderService.getDirectionsFor(Airline.WIZZAIR);
        Log.info("Checking {} directions", wizzairDirections.size());
        List<Callable<Void>> tasks = new ArrayList<>();
        for (Direction wizzairDirection : wizzairDirections) {
            tasks.add(new FindFlightsTask(webDriverMangerService, prepareUrl(wizzairDirection), jmsPublisher, wizzairDirection, daysToCheck));
        }
//        add other
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Log.error("Error during executing tasks", e);
        }


    }

    private String prepareUrl(Direction wizziarDirection) {
        return String.format(WIZZAIR_FLIGHTS_URL, wizziarDirection.getFromCode(), wizziarDirection.getToCode(), LocalDate.now().plusDays(1).toString());
    }

}
