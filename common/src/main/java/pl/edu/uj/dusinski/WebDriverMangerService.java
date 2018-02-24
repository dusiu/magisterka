package pl.edu.uj.dusinski;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WebDriverMangerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverMangerService.class);
    private int MAXIMUM_CAPACITY = 8;
    private BlockingQueue<WebDriver> webDrivers = new ArrayBlockingQueue<>(MAXIMUM_CAPACITY);
    private final String phantomPath = System.getProperty("user.dir") + "\\common\\src\\main\\resources\\phantomjs.exe";
    private final String chromePath = System.getProperty("user.dir") + "\\common\\src\\main\\resources\\chromedriver.exe";

    public WebDriverMangerService(int phantomJsNumber) {
        if (phantomJsNumber > 0) {
            this.webDrivers = new ArrayBlockingQueue<>(phantomJsNumber);
        }
        System.setProperty("phantomjs.binary.path", phantomPath);
        System.setProperty("webdriver.chrome.driver", chromePath);
        System.setProperty("webdriver.gecko.driver", "C:\\tools\\phantomJs\\bin\\geckodriver.exe");
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new String[]{
                "--webdriver-loglevel=NONE"
        };
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        for (int i = 0; i < phantomJsNumber; i++) {
//            webDrivers.add(new FirefoxDriver());
//            webDrivers.add(new PhantomJSDriver(dcap));
            webDrivers.add(new ChromeDriver());
        }
    }

    public WebDriver getFreePhantomJs() {
        return getWebDriver();
    }

    private WebDriver getWebDriver() {
        WebDriver take = null;
        try {
            take = webDrivers.take();
        } catch (InterruptedException e) {
            LOGGER.error("Error during waiting for phantomJs instance", e);
        }
        return take;
    }

    public void returnPhantomJs(WebDriver webDriver) {
        webDrivers.add(webDriver);
    }

    @PreDestroy
    public void cleanUp() {
        while (webDrivers != null && !webDrivers.isEmpty()) {
            getWebDriver().quit();
        }
    }

}
