package pl.edu.uj.dusinski.services;

import com.google.gson.Gson;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.FlightDetailsData;
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
import java.util.concurrent.atomic.AtomicInteger;

import static pl.edu.uj.dusinski.dao.Airline.RYANAIR;
import static pl.edu.uj.dusinski.dao.Airline.WIZZAIR;

@Service
@EnableScheduling
public class FlightsDetailsFinderService {
    private static final Logger Log = LoggerFactory.getLogger(FlightsDetailsFinderService.class);
    private static final String WIZZAIR_FLIGHTS_URL = "https://wizzair.com/en-gb#/booking/select-flight/%s/%s/%s";
    private static final String RYANAIR_FLIGHTS_URL = "https://www.ryanair.com/pl/pl/booking/home/%s/%s/%s";

    private final DirectionsProviderService directionsProviderService;
    private final WebDriverMangerService webDriverMangerService;
    private final ExecutorService executorService;
    private final JmsPublisher jmsPublisher;
    private final int daysToCheck;
    private static int submittedTask;
    private static AtomicInteger doneTask = new AtomicInteger(0);
    private final RestTemplate restTemplate;
    private final String url = "https://api.ryanair.com/farefinder/3/oneWayFares?&departureAirportIataCode=BCN&outboundDepartureDateFrom=2018-10-11&outboundDepartureDateTo=2018-10-28";
    private final Gson gson = new Gson();

    @Autowired
    public FlightsDetailsFinderService(DirectionsProviderService directionsProviderService,
                                       WebDriverMangerService webDriverMangerService,
                                       JmsPublisher jmsPublisher,
                                       @Value("${web.driver.instances}") int webDriverInstances,
                                       @Value("${days.to.check}") int daysToCheck, RestTemplate restTemplate) {
        this.directionsProviderService = directionsProviderService;
        this.webDriverMangerService = webDriverMangerService;
        this.jmsPublisher = jmsPublisher;
        this.executorService = Executors.newFixedThreadPool(webDriverInstances);
        this.daysToCheck = daysToCheck;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 10_000)
    public void findDirectionsDetails() {
        findAllFlights(RYANAIR, WIZZAIR);
    }

    private void findAllFlights(Airline... airlines) {

        FlightDetailsData flightDetailsData = gson.fromJson(restTemplate.getForObject(url, String.class), FlightDetailsData.class);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (Airline airline : airlines) {
            List<Direction> directions = directionsProviderService.getDirectionsFor(airline);
            Log.info("Adding {} {} directions to check", directions.size(), airline);
//        directions = directions.subList(376, directions.size());
            for (Direction direction : directions) {
//                tasks.add(createFindFlightTask(direction, airline));
            }
        }
        submittedTask = tasks.size();
        doneTask.set(0);
//        doneTask.set(376);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Log.error("Error during executing tasks", e);
        }
    }

    private FindFlightsTask createFindFlightTask(Direction direction, Airline airline) {
        if (WIZZAIR.equals(airline)) {
            return new FindFlightsTaskWizziar(webDriverMangerService, prepareUrl(direction, airline), jmsPublisher, direction, daysToCheck);
        }
        return new FindFlightsTaskRyanair(webDriverMangerService, prepareUrl(direction, airline), jmsPublisher, direction, daysToCheck);
    }

    static void logTaskFinished() {
        Log.info("{}/{} directions are checked", doneTask.incrementAndGet(), submittedTask);
    }

    private String prepareUrl(Direction direction, Airline airline) {
        String url = WIZZAIR.equals(airline) ? WIZZAIR_FLIGHTS_URL : RYANAIR_FLIGHTS_URL;
        return String.format(url, direction.getFromCode(), direction.getToCode(), LocalDate.now().plusDays(1).toString());
    }

    public static void waitForWebsite(WebDriver webDriver) {
        new WebDriverWait(webDriver, 30).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

}
