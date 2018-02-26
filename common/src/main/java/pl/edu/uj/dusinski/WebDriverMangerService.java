package pl.edu.uj.dusinski;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WebDriverMangerService {

    private static final Logger Log = LoggerFactory.getLogger(WebDriverMangerService.class);
    private int MAXIMUM_CAPACITY = 8;
    private BlockingQueue<WebDriver> webDrivers = new ArrayBlockingQueue<>(MAXIMUM_CAPACITY);
    private final String phantomPath = System.getProperty("user.dir") + "\\common\\src\\main\\resources\\phantomjs.exe";
    private final String chromePath = System.getProperty("user.dir") + "\\common\\src\\main\\resources\\chromedriver.exe";

    public WebDriverMangerService(int webDriverNumber) {
        if (webDriverNumber > 0) {
            this.webDrivers = new ArrayBlockingQueue<>(webDriverNumber);
        }
        System.setProperty("phantomjs.binary.path", phantomPath);
        System.setProperty("webdriver.chrome.driver", chromePath);
        System.setProperty("webdriver.gecko.driver", "C:\\tools\\phantomJs\\bin\\geckodriver.exe");
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new String[]{
                "--webdriver-loglevel=NONE"
        };
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        for (int i = 0; i < webDriverNumber; i++) {
//            webDrivers.add(new FirefoxDriver());
//            webDrivers.add(new PhantomJSDriver(dcap));
            ChromeDriver driver = new ChromeDriver();
//            WebDriver driver = new PhantomJSDriver(dcap);
//            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            webDrivers.add(driver);
        }
    }

    public WebDriver getFreeWebDriver() {
        return getWebDriver();
    }

    private WebDriver getWebDriver() {
        WebDriver driver = null;
        try {
            driver = webDrivers.take();
            ((JavascriptExecutor)driver).executeScript("window.open()");
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(1));
        } catch (InterruptedException e) {
            Log.error("Error during waiting for webDriver instance", e);
        }
        return driver;
    }

    public void returnWebDriver(WebDriver driver) {
        driver.close();
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(0));
        webDrivers.add(driver);
    }

    @PreDestroy
    public void cleanUp() {
        while (webDrivers != null && !webDrivers.isEmpty()) {
            getWebDriver().quit();
        }
    }

}
