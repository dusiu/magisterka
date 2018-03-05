package pl.edu.uj.dusinski.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pl.edu.uj.dusinski.dao.Airline.RYANAIR;
import static pl.edu.uj.dusinski.services.DirectionsUtils.waitUntilElementIsReady;

@Service
public class RyanairDirectionFinderService {
    private static final Logger Log = LoggerFactory.getLogger(RyanairDirectionFinderService.class);
    private static final String RYANAIR_DIRECTION_NAME = "https://www.ryanair.com/pl/pl/tanie-loty-miejsca-docelowe";
    private static final String RYANAIR_DIRECTIONS = "https://www.ryanair.com/pl/pl/lista-tanich-destynacji";

    private final WebDriverMangerService webDriverMangerService;
    private final JmsPublisher jmsPublisher;
    private final Map<String, String> nameCodeAirportMap = new HashMap<>();

    @Autowired
    public RyanairDirectionFinderService(WebDriverMangerService webDriverMangerService, JmsPublisher jmsPublisher) {
        this.webDriverMangerService = webDriverMangerService;
        this.jmsPublisher = jmsPublisher;
    }

    public boolean updateDirections() {
        Log.info("Updating list of the directions");

        WebDriver webDriver = null;
        try {
            webDriver = webDriverMangerService.getFreeWebDriver();

            setDirectionNameCodeMap(webDriver);
            findDirections(webDriver);
        } catch (Exception e) {
            Log.error("Exception during updating directions", e);
            return false;
        } finally {
            webDriverMangerService.returnWebDriver(webDriver);
        }
        return true;
    }

    private void setDirectionNameCodeMap(WebDriver webDriver) {
        webDriver.get(RYANAIR_DIRECTION_NAME);
        waitUntilElementIsReady(webDriver, By.className("zoom-control-minus"));
        List<String> airportCodes = webDriver.findElements(By.className("marker")).stream().map(v -> v.getAttribute("id")).collect(Collectors.toList());
        webDriver.get(RYANAIR_DIRECTIONS);
        openAirportList(webDriver);
        waitUntilElementIsReady(webDriver, By.cssSelector("div.pane.right"));
        List<String> airportNames = webDriver.findElement(By.cssSelector("div.pane.right")).findElements(By.tagName("div")).stream()
                .filter(v -> "core-list-item core-list-item-rounded".equals(v.getAttribute("class")))
                .map(WebElement::getText).collect(Collectors.toList());
        for (int i = 0; i < airportNames.size(); i++) {
            AirportDetails airportDetails = new AirportDetails(airportNames.get(i), airportNames.get(i), "", airportCodes.get(i), RYANAIR);
            jmsPublisher.publishAirportDetails(airportDetails);
            nameCodeAirportMap.put(airportDetails.getName(), airportDetails.getCode());
        }
    }

    private void findDirections(WebDriver webDriver) {
        webDriver.get(RYANAIR_DIRECTIONS);
        openAirportList(webDriver);
        waitUntilElementIsReady(webDriver, By.cssSelector("div.pane.right"));

        for (int i = 0; i < webDriver.findElement(By.cssSelector("div.pane.right")).findElements(By.tagName("div")).stream()
                .filter(v -> "core-list-item core-list-item-rounded".equals(v.getAttribute("class"))).count(); i++) {
            WebElement webElement = webDriver.findElement(By.cssSelector("div.pane.right")).findElements(By.tagName("div")).stream()
                    .filter(v -> "core-list-item core-list-item-rounded".equals(v.getAttribute("class"))).collect(Collectors.toList()).get(i);
            String from = webElement.getText();
            String fromCode = nameCodeAirportMap.get(from);
            openAirportList(webDriver);
            moveToElement(webDriver, webElement);
            waitUntilElementIsReady(webDriver, By.className("route-list-navigation-submit"));
            webDriver.findElement(By.className("route-list-navigation-submit")).findElement(By.tagName("button")).click();
            List<WebElement> countries = webDriver.findElements(By.cssSelector("li.city"));
            for (WebElement country : countries) {
                String to = country.getText();
                String toCode = nameCodeAirportMap.get(to);
                Direction direction = new Direction(fromCode + toCode + RYANAIR.getValue(), fromCode, toCode, RYANAIR);
                jmsPublisher.publishDirection(direction);
            }
            openAirportList(webDriver);
        }
    }

    private void moveToElement(WebDriver webDriver, WebElement webElement) {
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webElement);
        actions.perform();
        webElement.click();
    }

    private void openAirportList(WebDriver webDriver) {
        waitUntilElementIsReady(webDriver, By.cssSelector("body > div.FR > main > route-list > div > div.route-list-navigation > form > div > div.route-list-navigation-destination > div > div > div.col-departure-airport > div:nth-child(2) > div.field-type > div > div.disabled-wrap > input"));
        if (webDriver.findElements(By.cssSelector("div.core-list-item.core-list-item-rounded")).size() == 0) {
            WebElement element = webDriver.findElement(By.cssSelector("body > div.FR > main > route-list > div > div.route-list-navigation > form > div > div.route-list-navigation-destination > div > div > div.col-departure-airport > div:nth-child(2) > div.field-type > div > div.disabled-wrap > input"));
            element.click();
        }
        webDriver.findElements(By.cssSelector("div.core-list-item.core-list-item-rounded")).get(0).click();
    }

}
