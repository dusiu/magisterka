package pl.edu.uj.dusinski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.edu.uj.dusinski.dao.Airline.WIZZAIR;

@Service
@EnableScheduling
public class FlightsDetailsFinderService {
    private static final Logger Log = LoggerFactory.getLogger(FlightsDetailsFinderService.class);

    private final DirectionsProviderService directionsProviderService;
    private final WebDriverMangerService webDriverMangerService;
    private final ExecutorService executorService;
    private final JmsPublisher jmsPublisher;
    private final int daysToCheck;
    private static int submittedTask;
    private static AtomicInteger doneTask = new AtomicInteger(0);
    private final RestTemplate restTemplate;
    private final int halfADayInMs = 12 * 60 * 60 * 1000;
    private final int taskTimeout;

    @Autowired
    public FlightsDetailsFinderService(DirectionsProviderService directionsProviderService,
                                       WebDriverMangerService webDriverMangerService,
                                       JmsPublisher jmsPublisher,
                                       @Value("${web.driver.instances}") int webDriverInstances,
                                       @Value("${days.to.check}") int daysToCheck,
                                       RestTemplate restTemplate,
                                       @Value("${task.timeout.seconds}") int taskTimeout) {
        this.directionsProviderService = directionsProviderService;
        this.webDriverMangerService = webDriverMangerService;
        this.jmsPublisher = jmsPublisher;
        this.executorService = Executors.newFixedThreadPool(webDriverInstances);
        this.daysToCheck = daysToCheck;
        this.restTemplate = restTemplate;
        this.taskTimeout = taskTimeout;
    }

    @Scheduled(fixedDelay = halfADayInMs)
    public void findDirectionsDetails() {
//        findAllFlights(RYANAIR, WIZZAIR);
        findAllFlights(WIZZAIR);
    }

    private void findAllFlights(Airline... airlines) {
        List<Callable<Void>> tasks = new ArrayList<>();
        for (Airline airline : airlines) {
            List<Direction> directions = directionsProviderService.getDirectionsFor(airline);
            Log.info("Adding {} {} directions to check", directions.size(), airline);
        directions = directions.subList(424 + 44, directions.size());
            for (Direction direction : directions) {
                tasks.add(createFindFlightTask(direction, airline));
            }
        }
        submittedTask = tasks.size();
        doneTask.set(0);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Log.error("Error during executing tasks", e);
        }
    }

    private Callable<Void> createFindFlightTask(Direction direction, Airline airline) {
        if (WIZZAIR.equals(airline)) {
            return new FindFlightsTaskWizzair(webDriverMangerService, jmsPublisher, direction, daysToCheck, taskTimeout);
        }
        return new FindFlightsTaskRyanair(restTemplate, jmsPublisher, direction, daysToCheck, directionsProviderService);
    }

    static void logTaskFinished() {
        Log.info("{}/{} directions are checked", doneTask.incrementAndGet(), submittedTask);
    }

}
