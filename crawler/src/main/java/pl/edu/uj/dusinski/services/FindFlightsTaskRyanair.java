package pl.edu.uj.dusinski.services;

import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static pl.edu.uj.dusinski.CurrencyResolverUtils.resolveCurrencyFromSymbol;
import static pl.edu.uj.dusinski.services.FlightsDetailsFinderService.waitForWebsite;

public class FindFlightsTaskRyanair extends FindFlightsTask {
    private static final Logger Log = LoggerFactory.getLogger(FindFlightsTaskRyanair.class);

    private final static By SLIDER = By.className("slide");
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMyyyy");


    FindFlightsTaskRyanair(WebDriverMangerService webDriverMangerService, String url, JmsPublisher jmsPublisher, Direction direction, int daysToCheck) {
        super(webDriverMangerService, url, jmsPublisher, direction, daysToCheck);
    }

    protected void findFlightDetails(WebDriver webDriver) {
        webDriver.manage().deleteAllCookies();
        webDriver.get(url);
//        webDriver.get("https://www.ryanair.com/pl/pl/booking/home/CAG/PMF/2018-03-08");
        while (webDriver.findElements(SLIDER).isEmpty()) {
            Log.debug("There is no data in {} yet, waiting...", direction.getId());
            waitForWebsite(webDriver);
        }

        int checkedFlightsNo = 0;
        int publishedFlightsNo = 0;
        for (int i = 0; checkedFlightsNo < daysToCheck; ) {
            try {
                String dateString = webDriver.findElements(SLIDER).get(i).findElement(By.className("date")).getText();
                if (checkedFlightsNo > 0 && checkedFlightsNo % 5 == 0) {
                    WebElement webElement = webDriver.findElement(By.className("base-carousel")).findElements(By.tagName("button")).get(1);
                    waitUntilElementIsReady(webDriver, webElement);
                    while (!isDateParsable(dateString)) {
                        webElement.click();
                        waitUntilTextIsPresent(webDriver, webDriver.findElements(SLIDER).get(i).findElement(By.className("date")));
                        dateString = webDriver.findElements(SLIDER).get(i).findElement(By.className("date")).getText();
                    }
                }
                LocalDate date = parseDate(dateString);
                WebElement fareString = webDriver.findElements(SLIDER).get(i).findElement(By.className("fare"));
                while (!isPriceAvailable(fareString.getText())) {
                    waitUntilPriceAppear(webDriver, webDriver.findElements(SLIDER).get(i).findElement(By.className("fare")));
                    fareString = webDriver.findElements(SLIDER).get(i).findElement(By.className("fare"));
                }
                String priceWithCurrencySymbol = fareString.getText();
                if (isNotBlank(priceWithCurrencySymbol)) {
                    String price = getProperPrice(priceWithCurrencySymbol);
                    if (Double.valueOf(price) > 0) {
                        String currencySymbol = priceWithCurrencySymbol.replaceAll("[\\.,0123456789]", "");
                        String currency = resolveCurrencyFromSymbol(currencySymbol.trim(), direction.getFromCode());
                        FlightDetails flightDetails = new FlightDetails(direction.getId() + date, date, direction, Double.valueOf(price), currency);
                        Log.info("new flight details {}", flightDetails);
                        publishedFlightsNo++;
//                jmsPublisher.publishNewFlightDetails(flightDetails);
                    }
                }
            } catch (StaleElementReferenceException e) {
                i--;
                Log.debug("Stale element exception {}", direction.getId());
                waitForWebsite(webDriver);
            }
            checkedFlightsNo++;
            i++;
            i = i % 15;
        }
        Log.info("Published: {} flights into direction: {}", publishedFlightsNo, direction.getId());
    }

    private boolean isPriceAvailable(String fareString) {
        fareString = getProperPrice(fareString);
        return fareString.isEmpty() || (NumberUtils.isCreatable(fareString) && Double.valueOf(fareString) != 0.0);
    }

    private String getProperPrice(String fareString) {
        return fareString.replaceAll(",", ".").replaceAll("[^\\.0123456789]", "");
    }

    private boolean isDateParsable(String dateString) {
        try {
            parseDate(dateString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private LocalDate parseDate(String dateString) {
        return LocalDate.parse((dateString.toLowerCase() + LocalDate.now().getYear()).substring(dateString.indexOf(" ") + 1), FORMATTER);
    }

    void waitUntilElementIsReady(WebDriver driver, WebElement by) {
        new WebDriverWait(driver, 30)
                .until(ExpectedConditions.elementToBeClickable(by));
    }

    void waitUntilTextIsPresent(WebDriver driver, WebElement webElement) {
        try {
            new WebDriverWait(driver, 3)
                    .until((webDriver -> isNotBlank(webElement.getText())));
        } catch (TimeoutException e) {

        }
    }

    private void waitUntilPriceAppear(WebDriver driver, WebElement webElement) {
        try {
            new WebDriverWait(driver, 3)
                    .until((webDriver -> webElement.getText()));
        } catch (TimeoutException e) {

        }
    }

}
