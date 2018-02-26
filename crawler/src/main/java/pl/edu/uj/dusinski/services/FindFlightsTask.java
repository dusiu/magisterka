package pl.edu.uj.dusinski.services;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.uj.dusinski.CurrencyResolverUtils;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

public class FindFlightsTask implements Callable<Void> {
    private static final Logger Log = LoggerFactory.getLogger(FindFlightsTask.class);

    private final WebDriverMangerService webDriverMangerService;
    private final String url;
    private final JmsPublisher jmsPublisher;
    private final Direction direction;
    private final int daysToCheck;

    public FindFlightsTask(WebDriverMangerService webDriverMangerService, String url, JmsPublisher jmsPublisher, Direction direction, int daysToCheck) {
        this.webDriverMangerService = webDriverMangerService;
        this.url = url;
        this.jmsPublisher = jmsPublisher;
        this.direction = direction;
        this.daysToCheck = daysToCheck;
    }

    @Override
    public Void call() {
        WebDriver webDriver = webDriverMangerService.getFreeWebDriver();
        Log.info("Checking url {}", url);
        Thread t = new Thread(() -> {
            webDriver.manage().deleteAllCookies();
            webDriver.get(url);
            new WebDriverWait(webDriver, 30).until((ExpectedCondition<Boolean>) wd ->
                    ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));

            List<WebElement> dailyFlightsInfo = webDriver.findElements(By.className("booking-flow__flight-select__chart__day__info"));
            if (dailyFlightsInfo.isEmpty()) {
                Log.info("There is no data in {}", direction.getId());
                return;
            }
            while (dailyFlightsInfo.size() < 15) {
                clickForNext(webDriver);
                dailyFlightsInfo = webDriver.findElements(By.className("booking-flow__flight-select__chart__day__info"));
            }
            for (WebElement dailyInfo : dailyFlightsInfo) {
                String dateTime = dailyInfo.findElement(By.tagName("time")).getAttribute("datetime");
                LocalDate date = LocalDate.parse(dateTime.substring(0, dateTime.indexOf("T")));
                String symbol = "";
                CurrencyResolverUtils.resolveCurrencyFromSymbol(symbol, direction.getFromCode());
                try {
                    String priceWithCurrencySymbol = dailyInfo.findElement(By.className("booking-flow__flight-select__chart__day__price")).getText();
                    String price = priceWithCurrencySymbol.replaceAll("[^\\.0123456789]", "");
                    String currencySymbol = priceWithCurrencySymbol.replaceAll("[\\.,0123456789]", "");
                    String currency = CurrencyResolverUtils.resolveCurrencyFromSymbol(currencySymbol, direction.getFromCode());
                    jmsPublisher.publishNewFlightDetails(new FlightDetails(direction.getId() + date, date, direction, Double.valueOf(price), currency));
                } catch (NoSuchElementException e) {
                    Log.debug("There isn't flight for {} in {}", direction.getId(), date);
                }
            }
        }, Thread.currentThread().getName());
        t.start();
        try {
            t.join(30_000);
        } catch (InterruptedException e) {
        }
        if (t.isAlive()) {
            Log.warn("Timeout on loading page " + url);
            t.interrupt();
        }
        returnWebDriver(webDriver);
        return null;
    }

    private void clickForNext(WebDriver webDriver) {
        WebElement nextButton = webDriver.findElement(By.cssSelector("i.icon.icon__arrow--toright--pink")).findElement(By.xpath(".."));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(nextButton).click().perform();
    }

    private void returnWebDriver(WebDriver webDriver) {
        webDriverMangerService.returnWebDriver(webDriver);
    }
}
