package pl.edu.uj.dusinski.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.DirectionRefreshDetails;
import pl.edu.uj.dusinski.jpa.AirportRepository;
import pl.edu.uj.dusinski.jpa.DirectionRefreshDetailsRepository;
import pl.edu.uj.dusinski.jpa.DirectionRepository;

import java.time.LocalDateTime;
import java.util.*;

import static pl.edu.uj.dusinski.dao.Airline.WIZZIAR;

@Service
@DependsOn("webDriverMangerService")
public class DirectionFinderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverMangerService.class);
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String WIZZIAR_FLIGHTS = "https://wizzair.com/en-gb/flights";
    private static final int WAIT_TIMEOUT = 30;
    private final WebDriverMangerService webDriverMangerService;
    private final AirportRepository airportRepository;
    private final DirectionRepository directionRepository;
    private final DirectionRefreshDetailsRepository directionRefreshDetails;

    @Autowired
    public DirectionFinderService(WebDriverMangerService webDriverMangerService,
                                  AirportRepository airportRepository,
                                  DirectionRepository directionRepository,
                                  DirectionRefreshDetailsRepository directionRefreshDetails) {
        this.webDriverMangerService = webDriverMangerService;
        this.airportRepository = airportRepository;
        this.directionRepository = directionRepository;
        this.directionRefreshDetails = directionRefreshDetails;
    }

    public void updateDirections() {
        LOGGER.info("Updating list of the directions");

        WebDriver phantomJs = null;
        List<AirportDetails> airportsDetails = new ArrayList<>();
        List<Direction> directions = new ArrayList<>();
        try {
            phantomJs = webDriverMangerService.getFreePhantomJs();
            List<WebElement> airports = getAirports(phantomJs);
            List<Map.Entry<String, String>> airportsUrlNameList = new ArrayList<>();
            for (WebElement airport : airports) {
                airportsUrlNameList.add(
                        new AbstractMap.SimpleImmutableEntry<>(airport.getAttribute("href"), airport.getText()));
            }
            int i = 0;
//            airportsUrlNameList = airportsUrlNameList.subList(0, 20);
            for (Map.Entry<String, String> airportUrlName : airportsUrlNameList) {
                directions.addAll(getAirportDirections(airportUrlName.getKey(), airportUrlName.getValue(), phantomJs, airportsDetails));
                LOGGER.info("Checked {} from {} airports", ++i, airportsUrlNameList.size());
            }
            LOGGER.info("Updated {} directions", directions.size());
            airportRepository.deleteAll();
            airportRepository.saveAll(airportsDetails);
            directionRepository.deleteAll();
            directionRepository.saveAll(directions);
            directionRefreshDetails.save(new DirectionRefreshDetails(directionRefreshDetails.count(), LocalDateTime.now(), directions.size()));
        } catch (Exception e) {
            LOGGER.error("Exception during updating directions", e);
        } finally {
            webDriverMangerService.returnPhantomJs(phantomJs);
        }
    }

    private List<WebElement> getAirports(WebDriver phantomJs) {
        phantomJs.get(WIZZIAR_FLIGHTS);
        By elem = By.className("list--destinations-cities");
        waitUntilElementIsReady(phantomJs, elem);
        List<WebElement> countries = phantomJs.findElements(elem);
        List<WebElement> airports = new ArrayList<>();
        for (WebElement country : countries) {
            airports.addAll(country.findElements(By.tagName("a")));
        }
        return airports;
    }

    private void waitUntilElementIsReady(WebDriver driver, By by) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(by));
    }

    private List<Direction> getAirportDirections(String airportUrl, String airportName, WebDriver phantomJs, List<AirportDetails> airportsDetails) {
        LOGGER.info("checking airport: {} with url: {}", airportName, airportUrl);
        try {
            List<Direction> directions = new ArrayList<>();
            phantomJs.get(airportUrl);
            waitUntilElementIsReady(phantomJs, By.className("gr-7"));
            String fullNameWithCode = phantomJs.findElement(By.className("gr-7")).getText().split("\\n")[0];
            String airportFullName = fullNameWithCode.substring(0, fullNameWithCode.lastIndexOf(OPEN_BRACKET));
            String fromCode = fullNameWithCode
                    .substring(fullNameWithCode.lastIndexOf(OPEN_BRACKET) + 1, fullNameWithCode.lastIndexOf(CLOSE_BRACKET));
            airportsDetails.add(new AirportDetails(airportName, airportFullName, "", fromCode, WIZZIAR));
            waitUntilElementIsReady(phantomJs, By.id("search-departure-station"));
            phantomJs.findElement(By.id("search-departure-station")).click();
            waitUntilElementIsReady(phantomJs, By.className("locations-container__location"));
            List<WebElement> toAirports = phantomJs.findElements(By.className("locations-container__location"));
            for (WebElement toAirport : toAirports) {
                String[] toAirportDetails = toAirport.getText().split("\\s");
                String toCode = toAirportDetails[toAirportDetails.length - 1];
                if (!toCode.isEmpty()) {
                    directions.add(new Direction(fromCode + toCode, fromCode, toCode, WIZZIAR));
                }
            }
            return directions;
        } catch (Exception e) {
            LOGGER.info("Error during getting airport {} direction", airportName, e);
        }
        return Collections.emptyList();
    }

}
