package pl.edu.uj.dusinski.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DirectionsUtils {
    private static final int WAIT_TIMEOUT = 30;

    public static void waitUntilElementIsReady(WebDriver driver, By by) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(by));
    }

}
