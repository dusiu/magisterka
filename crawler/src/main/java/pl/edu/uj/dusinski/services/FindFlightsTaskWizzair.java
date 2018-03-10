package pl.edu.uj.dusinski.services;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import static pl.edu.uj.dusinski.CurrencyResolverUtils.resolveCurrencyFromSymbol;
import static pl.edu.uj.dusinski.services.FlightsDetailsFinderService.logTaskFinished;

public class FindFlightsTaskWizzair implements Callable<Void> {
    private static final Logger Log = LoggerFactory.getLogger(FindFlightsTaskWizzair.class);
    private static final String WIZZAIR_FLIGHTS_URL = "https://wizzair.com/en-gb#/booking/select-flight/%s/%s/%s";

    private final WebDriverMangerService webDriverMangerService;
    private final String url;
    private final JmsPublisher jmsPublisher;
    private final Direction direction;
    private final int daysToCheck;
    private final String infoClassName = "booking-flow__flight-select__chart__day__info";

    FindFlightsTaskWizzair(WebDriverMangerService webDriverMangerService, JmsPublisher jmsPublisher, Direction direction, int daysToCheck) {
        this.webDriverMangerService = webDriverMangerService;
        this.url = String.format(WIZZAIR_FLIGHTS_URL, direction.getFromCode(), direction.getToCode(), LocalDate.now().plusDays(1).toString());
        this.jmsPublisher = jmsPublisher;
        this.direction = direction;
        this.daysToCheck = daysToCheck;
    }

    @Override
    public Void call() {
        WebDriver webDriver = webDriverMangerService.getFreeWebDriver();
        Log.info("Checking url {}", url);
        Thread t = new Thread(() -> findFlightDetails(webDriver), Thread.currentThread().getName());
        t.start();
        try {
            t.join(90_000);
        } catch (InterruptedException e) {
        }
        if (t.isAlive()) {
            Log.error("Timeout on loading page " + url);
            t.interrupt();
        }
        webDriverMangerService.returnWebDriver(webDriver);
        logTaskFinished();
        return null;
    }

    private void findFlightDetails(WebDriver webDriver) {
        webDriver.manage().deleteAllCookies();
        webDriver.get(url);

        while (webDriver.findElements(By.className(infoClassName)).isEmpty()) {
            Log.debug("There is no data in {} yet, waiting...", direction.getId());
            waitForWebsite(webDriver);
        }
        while (webDriver.findElements(By.className(infoClassName)).size() < daysToCheck) {
            clickForNext(webDriver);
        }
        waitForWebsite(webDriver);
        int directionNo = 0;
        for (int i = 0; i < webDriver.findElements(By.className(infoClassName)).size(); i++) {
            try {
                String dateTime = webDriver.findElements(By.className(infoClassName)).get(i).findElement(By.tagName("time")).getAttribute("datetime");
                LocalDate date = LocalDate.parse(dateTime.substring(0, dateTime.indexOf("T")));
                try {
                    String priceWithCurrencySymbol = webDriver.findElements(By.className(infoClassName)).get(i)
                            .findElement(By.className("booking-flow__flight-select__chart__day__price")).getText();
                    String price = priceWithCurrencySymbol.replaceAll("[^\\.0123456789]", "");
                    String currencySymbol = priceWithCurrencySymbol.replaceAll("[\\.,0123456789]", "");
                    String currency = resolveCurrencyFromSymbol(currencySymbol, direction.getFromCode());
                    jmsPublisher.publishNewFlightDetails(new FlightDetails(direction.getId() + date, date, direction, Double.valueOf(price), currency));
                    directionNo++;
                } catch (NoSuchElementException e) {
                    Log.debug("There isn't flight for {} in {}", direction.getId(), date);
                }
            } catch (StaleElementReferenceException e) {
                i--;
                Log.debug("Stale element exception {}", direction.getId());
                waitForWebsite(webDriver);
            }
        }
        Log.info("Published: {} flights into direction: {}", directionNo, direction.getId());
    }

    private void clickForNext(WebDriver webDriver) {
        WebElement nextButton = webDriver.findElement(By.cssSelector("i.icon.icon__arrow--toright--pink")).findElement(By.xpath(".."));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(nextButton).click().perform();
    }

    private void waitForWebsite(WebDriver webDriver) {
        new WebDriverWait(webDriver, 30).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }
}
