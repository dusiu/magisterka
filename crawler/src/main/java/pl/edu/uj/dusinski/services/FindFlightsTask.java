package pl.edu.uj.dusinski.services;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.uj.dusinski.JmsPublisher;
import pl.edu.uj.dusinski.WebDriverMangerService;
import pl.edu.uj.dusinski.dao.Direction;

import java.util.concurrent.Callable;

import static pl.edu.uj.dusinski.services.FlightsDetailsFinderService.logTaskFinished;

public abstract class FindFlightsTask implements Callable<Void> {
    private static final Logger Log = LoggerFactory.getLogger(FindFlightsTask.class);

    protected final WebDriverMangerService webDriverMangerService;
    protected final String url;
    protected final JmsPublisher jmsPublisher;
    protected final Direction direction;
    protected final int daysToCheck;

    FindFlightsTask(WebDriverMangerService webDriverMangerService, String url, JmsPublisher jmsPublisher, Direction direction, int daysToCheck) {
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
        Thread t = new Thread(() -> findFlightDetails(webDriver), Thread.currentThread().getName());
        t.start();
        try {
            t.join(90000_000);
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

    protected abstract void findFlightDetails(WebDriver webDriver);

}
