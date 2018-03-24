package pl.edu.uj.dusinski;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
    private final String chromePath = System.getProperty("user.dir") + "/../webapps/directionManager/WEB-INF/classes/chromeDriver.exe";

    public WebDriverMangerService(int webDriverNumber) {
        if (webDriverNumber > 0) {
            this.webDrivers = new ArrayBlockingQueue<>(webDriverNumber);
        }
        Log.info("chrome path:" + chromePath);
        System.setProperty("webdriver.chrome.driver", chromePath);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("user-agent=user_agent");
        for (int i = 0; i < webDriverNumber; i++) {
            WebDriver driver = new ChromeDriver(chromeOptions);
            webDrivers.add(driver);
        }
    }

    public WebDriver getFreeWebDriver() throws InterruptedException {
        return getWebDriver();
    }

    private WebDriver getWebDriver() throws InterruptedException {
        WebDriver driver = webDrivers.take();
        ((JavascriptExecutor) driver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        return driver;
    }

    public void returnWebDriver(WebDriver driver) {
        driver.close();
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(0));
        webDrivers.add(driver);
    }

    @PreDestroy
    public void cleanUp() throws InterruptedException {
        while (webDrivers != null && !webDrivers.isEmpty()) {
            WebDriver webDriver = getWebDriver();
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }

}
