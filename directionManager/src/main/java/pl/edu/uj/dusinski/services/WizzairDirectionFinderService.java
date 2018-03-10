package pl.edu.uj.dusinski.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.Direction;

import java.util.*;

import static pl.edu.uj.dusinski.dao.Airline.WIZZAIR;
import static pl.edu.uj.dusinski.services.DirectionsUtils.waitUntilElementIsReady;

@Service
public class WizzairDirectionFinderService {

    private static final Logger Log = LoggerFactory.getLogger(WizzairDirectionFinderService.class);
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String WIZZIAR_FLIGHTS = "https://wizzair.com/en-gb/flights";
    private final WebDriverMangerService webDriverMangerService;
    private final JmsPublisher jmsPublisher;

    @Autowired
    public WizzairDirectionFinderService(WebDriverMangerService webDriverMangerService,
                                         JmsPublisher jmsPublisher) {
        this.webDriverMangerService = webDriverMangerService;
        this.jmsPublisher = jmsPublisher;
    }

    public boolean updateDirections() {
        Log.info("Updating list of the directions");

        WebDriver webDriver = null;
        try {
            webDriver = webDriverMangerService.getFreeWebDriver();
            List<WebElement> airports = getAirports(webDriver);
            List<Map.Entry<String, String>> airportsUrlNameList = new ArrayList<>();
            for (WebElement airport : airports) {
                airportsUrlNameList.add(
                        new AbstractMap.SimpleImmutableEntry<>(airport.getAttribute("href"), airport.getText()));
            }
            int i = 0;
            for (Map.Entry<String, String> airportUrlName : airportsUrlNameList) {
                getAirportDirections(airportUrlName.getKey(), airportUrlName.getValue(), webDriver);
                Log.info("Checked {} from {} airports", ++i, airportsUrlNameList.size());
            }
        } catch (Exception e) {
            Log.error("Exception during updating directions", e);
            return false;
        } finally {
            webDriverMangerService.returnWebDriver(webDriver);
        }
        return true;
    }

    private List<WebElement> getAirports(WebDriver webDriver) {
        webDriver.get(WIZZIAR_FLIGHTS);
        By elem = By.className("list--destinations-cities");
        waitUntilElementIsReady(webDriver, elem);
        List<WebElement> countries = webDriver.findElements(elem);
        List<WebElement> airports = new ArrayList<>();
        for (WebElement country : countries) {
            airports.addAll(country.findElements(By.tagName("a")));
        }
        return airports;
    }

    private List<Direction> getAirportDirections(String airportUrl, String airportName, WebDriver webDriver) {
        Log.info("checking airport: {} with url: {}", airportName, airportUrl);
        try {
            List<Direction> directions = new ArrayList<>();
            webDriver.get(airportUrl);
            waitUntilElementIsReady(webDriver, By.className("gr-7"));
            String fullNameWithCode = webDriver.findElement(By.className("gr-7")).getText().split("\\n")[0];
            String airportFullName = fullNameWithCode.substring(0, fullNameWithCode.lastIndexOf(OPEN_BRACKET)).trim();
            String fromCode = fullNameWithCode
                    .substring(fullNameWithCode.lastIndexOf(OPEN_BRACKET) + 1, fullNameWithCode.lastIndexOf(CLOSE_BRACKET));
            jmsPublisher.publishAirportDetails(new AirportDetails(airportName, airportFullName, "", fromCode, WIZZAIR));
            waitUntilElementIsReady(webDriver, By.id("search-departure-station"));
            webDriver.findElement(By.id("search-departure-station")).click();
            waitUntilElementIsReady(webDriver, By.className("locations-container__location"));
            List<WebElement> toAirports = webDriver.findElements(By.className("locations-container__location"));
            for (WebElement toAirport : toAirports) {
                String[] toAirportDetails = toAirport.getText().split("\\s");
                String toCode = toAirportDetails[toAirportDetails.length - 1];
                if (!toCode.isEmpty()) {
                    jmsPublisher.publishDirection(new Direction(fromCode + toCode + WIZZAIR.getValue(), fromCode, toCode, WIZZAIR));
                }
            }
            return directions;
        } catch (Exception e) {
            Log.info("Error during getting airport {} direction", airportName, e);
        }
        return Collections.emptyList();
    }

}
