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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    private final AtomicInteger taskTimeout;

    FindFlightsTaskWizzair(WebDriverMangerService webDriverMangerService,
                           JmsPublisher jmsPublisher,
                           Direction direction,
                           int daysToCheck,
                           int taskTimeout) {
        this.webDriverMangerService = webDriverMangerService;
        this.url = String.format(WIZZAIR_FLIGHTS_URL, direction.getFromCode(), direction.getToCode(), LocalDate.now().plusDays(1).toString());
        this.jmsPublisher = jmsPublisher;
        this.direction = direction;
        this.daysToCheck = daysToCheck;
        this.taskTimeout = new AtomicInteger(taskTimeout);
    }

    @Override
    public Void call() {
        AtomicReference<WebDriver> webDriver = new AtomicReference<>();
        try {
            webDriver.set(webDriverMangerService.getFreeWebDriver());
        } catch (InterruptedException e) {
            Log.error("Error during getting webdriver", e);
        }
        AtomicBoolean shouldWaitForInterrupt = new AtomicBoolean();
        Log.info("Checking url {}", url);
        Thread t = new Thread(() -> findFlightDetails(webDriver.get(), taskTimeout, shouldWaitForInterrupt), Thread.currentThread().getName());
        t.start();
        waitForJoin(t);
        if (shouldWaitForInterrupt.get()) {
            waitForJoin(t);
        }
        if (t.isAlive()) {
            Log.error("Timeout on loading page " + url);
            t.interrupt();
        }
        webDriverMangerService.returnWebDriver(webDriver.get());
        logTaskFinished();
        return null;
    }

    private void waitForJoin(Thread t) {
        try {
            t.join(taskTimeout.get());
        } catch (InterruptedException e) {
        }
    }

    private void findFlightDetails(WebDriver webDriver, AtomicInteger taskTimeout, AtomicBoolean shouldWaitForInterrupt) {
        long start = System.currentTimeMillis();
        webDriver.manage().deleteAllCookies();
        webDriver.get(url);
        int loadingTimeout = taskTimeout.get() * 10;
        while (webDriver.findElements(By.className(infoClassName)).isEmpty()) {
            waitForWebsite(webDriver);
            Log.debug("There is no data in {} yet, waiting...", direction.getId());
        }
        while (webDriver.findElements(By.className(infoClassName)).size() < daysToCheck) {
            if (webDriver.findElements(By.className(infoClassName)).size() > 0 && taskTimeout.get() != loadingTimeout) {
                taskTimeout.set(loadingTimeout);
                shouldWaitForInterrupt.set(true);
            }
            Log.debug("Waiting for {} days, currently {}, {}", daysToCheck,
                    webDriver.findElements(By.className(infoClassName)).size(), direction.getId());
            clickForNext(webDriver);
        }
        waitForWebsite(webDriver);
        Log.info("Took {} ms to load all details, {}", System.currentTimeMillis() - start, direction.getId());
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
        waitForWebsite(webDriver);
        WebElement nextButton = webDriver.findElement(By.cssSelector("i.icon.icon__arrow--toright--pink")).findElement(By.xpath(".."));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(nextButton).click().perform();
    }

    private void waitForWebsite(WebDriver webDriver) {
        boolean completed;
        do {
            completed = new WebDriverWait(webDriver, 30).until((ExpectedCondition<Boolean>) wd ->
                    ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"))
                    && !containLoader(webDriver);
        } while (!completed);
    }

    private boolean containLoader(WebDriver webDriver) {
        while (true) {
            try {
                WebElement element = webDriver.findElement(By.className("booking-flow__flight-select__chart"));
                if (!element.getAttribute("class").contains("loader-combined")) {
                    break;
                }
            } catch (Exception ex) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
        return false;
    }
}
